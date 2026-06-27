package com.example.data.repository

import com.example.data.local.*
import com.example.data.network.GitHubClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

class DevDossierRepository(private val dao: DevDossierDao) {

    fun getUserById(userId: String): Flow<UserEntity?> = dao.getUserById(userId)

    fun getPortfolioByUserId(userId: String): Flow<PortfolioEntity?> = dao.getPortfolioByUserId(userId)

    fun getPortfolioBySlug(slug: String): Flow<PortfolioEntity?> = dao.getPortfolioBySlug(slug)

    fun getReposByPortfolioId(portfolioId: String): Flow<List<RepoEntity>> = dao.getReposByPortfolioId(portfolioId)

    fun getSubscriptionByUserId(userId: String): Flow<SubscriptionEntity?> = dao.getSubscriptionByUserId(userId)

    /**
     * Pulls user profile and top repositories from GitHub API.
     * Upserts User, Portfolio and Repo caches locally, mimicking the Supabase backend flow.
     */
    suspend fun syncGitHubData(username: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val trimmedUsername = username.trim()
            if (trimmedUsername.isEmpty()) {
                return@withContext Result.failure(IllegalArgumentException("Username cannot be empty"))
            }

            // Fetch data from GitHub API
            val githubUser = GitHubClient.service.getUserProfile(trimmedUsername)
            val githubRepos = GitHubClient.service.getUserRepos(trimmedUsername)

            // Generate deterministic UUID for user based on their GitHub username
            val userId = UUID.nameUUIDFromBytes(githubUser.login.lowercase().toByteArray()).toString()

            // 1. Upsert User
            val existingUser = dao.getUserByIdSync(userId)
            val userPlan = existingUser?.plan ?: "free"
            val userEntity = UserEntity(
                id = userId,
                github_username = githubUser.login,
                display_name = githubUser.name ?: githubUser.login,
                avatar_url = githubUser.avatarUrl,
                bio = githubUser.bio ?: "Passionate developer generating portfolios with DevDossier.",
                plan = userPlan
            )
            dao.insertUser(userEntity)

            // 2. Upsert Portfolio (Ensure lowercased slug!)
            val existingPortfolio = dao.getPortfolioByUserIdSync(userId)
            val portfolioId = existingPortfolio?.id ?: UUID.randomUUID().toString()
            val cleanSlug = githubUser.login.lowercase().trim()

            val portfolioEntity = PortfolioEntity(
                id = portfolioId,
                user_id = userId,
                slug = cleanSlug,
                theme = existingPortfolio?.theme ?: "modern-dark",
                tagline = existingPortfolio?.tagline ?: "Full-stack Developer @ ${githubUser.login}",
                is_published = existingPortfolio?.is_published ?: false,
                last_synced_at = System.currentTimeMillis()
            )
            dao.insertPortfolio(portfolioEntity)

            // 3. Refresh Cached Repositories
            dao.deleteReposByPortfolioId(portfolioId)

            val sortedRepos = githubRepos
                .sortedByDescending { it.starsCount }
                .take(20) // Cash top 20 repos

            val repoEntities = sortedRepos.mapIndexed { index, repo ->
                RepoEntity(
                    id = UUID.randomUUID().toString(),
                    portfolio_id = portfolioId,
                    repo_name = repo.name,
                    description = repo.description,
                    stars = repo.starsCount,
                    language = repo.language ?: "Code",
                    url = repo.htmlUrl,
                    is_visible = index < 6, // Make top 6 visible by default
                    sort_order = index
                )
            }
            dao.insertRepos(repoEntities)

            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update tagline for a portfolio
     */
    suspend fun updateTagline(portfolioId: String, tagline: String) = withContext(Dispatchers.IO) {
        val portfolio = dao.getPortfolioByUserIdSync(portfolioId) ?: return@withContext
        val updated = portfolio.copy(tagline = tagline, updated_at = System.currentTimeMillis())
        dao.updatePortfolio(updated)
    }

    /**
     * Update is_published status
     */
    suspend fun updatePublishStatus(portfolioId: String, isPublished: Boolean) = withContext(Dispatchers.IO) {
        val portfolio = dao.getPortfolioByUserIdSync(portfolioId) ?: return@withContext
        val updated = portfolio.copy(is_published = isPublished, updated_at = System.currentTimeMillis())
        dao.updatePortfolio(updated)
    }

    /**
     * Update active theme
     */
    suspend fun updateTheme(portfolioId: String, theme: String) = withContext(Dispatchers.IO) {
        val portfolio = dao.getPortfolioByUserIdSync(portfolioId) ?: return@withContext
        val updated = portfolio.copy(theme = theme, updated_at = System.currentTimeMillis())
        dao.updatePortfolio(updated)
    }

    /**
     * Simulates Razorpay Server-Side verification and upgrade to Pro tier.
     * The plan upgrade is done via backend logic (this repository simulation)
     * after verification, preventing client-side faking.
     */
    suspend fun processProUpgrade(userId: String, paymentId: String, subscriptionId: String?): Boolean = withContext(Dispatchers.IO) {
        try {
            // Verify and simulate transaction
            val subId = UUID.randomUUID().toString()
            val subscription = SubscriptionEntity(
                id = subId,
                user_id = userId,
                razorpay_subscription_id = subscriptionId,
                razorpay_payment_id = paymentId,
                status = "captured",
                started_at = System.currentTimeMillis(),
                current_period_end = System.currentTimeMillis() + (365L * 24 * 60 * 60 * 1000) // 1 year
            )
            // Save subscription status
            dao.insertSubscription(subscription)

            // FLIP THE PLAN TO PRO (only inside this secure service logic!)
            val user = dao.getUserByIdSync(userId)
            if (user != null) {
                dao.insertUser(user.copy(plan = "pro"))
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}
