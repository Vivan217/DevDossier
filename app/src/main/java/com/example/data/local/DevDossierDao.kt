package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DevDossierDao {

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserByIdSync(userId: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM portfolios WHERE user_id = :userId")
    fun getPortfolioByUserId(userId: String): Flow<PortfolioEntity?>

    @Query("SELECT * FROM portfolios WHERE user_id = :userId")
    suspend fun getPortfolioByUserIdSync(userId: String): PortfolioEntity?

    @Query("SELECT * FROM portfolios WHERE slug = :slug")
    fun getPortfolioBySlug(slug: String): Flow<PortfolioEntity?>

    @Query("SELECT * FROM portfolios WHERE slug = :slug LIMIT 1")
    suspend fun getPortfolioBySlugSync(slug: String): PortfolioEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPortfolio(portfolio: PortfolioEntity)

    @Update
    suspend fun updatePortfolio(portfolio: PortfolioEntity)

    @Query("SELECT * FROM repos WHERE portfolio_id = :portfolioId ORDER BY sort_order ASC")
    fun getReposByPortfolioId(portfolioId: String): Flow<List<RepoEntity>>

    @Query("SELECT * FROM repos WHERE portfolio_id = :portfolioId ORDER BY sort_order ASC")
    suspend fun getReposByPortfolioIdSync(portfolioId: String): List<RepoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepos(repos: List<RepoEntity>)

    @Query("DELETE FROM repos WHERE portfolio_id = :portfolioId")
    suspend fun deleteReposByPortfolioId(portfolioId: String)

    @Query("SELECT * FROM subscriptions WHERE user_id = :userId")
    fun getSubscriptionByUserId(userId: String): Flow<SubscriptionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: SubscriptionEntity)
}
