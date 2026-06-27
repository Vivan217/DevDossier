package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.PortfolioEntity
import com.example.data.local.RepoEntity
import com.example.data.local.UserEntity
import com.example.data.repository.DevDossierRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DevDossierViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DevDossierRepository

    // Current logged-in / active user session
    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = DevDossierRepository(database.devDossierDao())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val userState: StateFlow<UserEntity?> = _currentUserId
        .flatMapLatest { id ->
            if (id == null) flowOf(null) else repository.getUserById(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val portfolioState: StateFlow<PortfolioEntity?> = _currentUserId
        .flatMapLatest { id ->
            if (id == null) flowOf(null) else repository.getPortfolioByUserId(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val reposState: StateFlow<List<RepoEntity>> = portfolioState
        .flatMapLatest { p ->
            if (p == null) flowOf(emptyList()) else repository.getReposByPortfolioId(p.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Trigger GitHub sync and set the active session user ID.
     */
    suspend fun syncGitHub(username: String): Result<String> {
        val result = repository.syncGitHubData(username)
        if (result.isSuccess) {
            _currentUserId.value = result.getOrThrow()
        }
        return result
    }

    /**
     * Re-fetch the current active user's GitHub details.
     */
    suspend fun refreshCurrentProfile() {
        val currentUsername = userState.value?.github_username ?: return
        repository.syncGitHubData(currentUsername)
    }

    fun updateTagline(tagline: String) {
        val userId = _currentUserId.value ?: return
        viewModelScope.launch {
            repository.updateTagline(userId, tagline)
        }
    }

    fun updateTheme(theme: String) {
        val userId = _currentUserId.value ?: return
        viewModelScope.launch {
            repository.updateTheme(userId, theme)
        }
    }

    fun updatePublishStatus(isPublished: Boolean) {
        val userId = _currentUserId.value ?: return
        viewModelScope.launch {
            repository.updatePublishStatus(userId, isPublished)
        }
    }

    fun toggleRepoVisibility(repo: RepoEntity) {
        viewModelScope.launch {
            val database = AppDatabase.getDatabase(getApplication())
            val updated = repo.copy(is_visible = !repo.is_visible)
            database.devDossierDao().insertRepos(listOf(updated))
        }
    }

    /**
     * Verifies simulated payment status securely (mimicking Razorpay webhook signature verify).
     */
    fun upgradePro(paymentId: String) {
        val userId = _currentUserId.value ?: return
        viewModelScope.launch {
            repository.processProUpgrade(userId, paymentId, subscriptionId = "sub_premium")
        }
    }

    fun logout() {
        _currentUserId.value = null
    }
}
