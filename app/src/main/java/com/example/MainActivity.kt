package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.DevDossierViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        val viewModel: DevDossierViewModel = viewModel()

        val userId by viewModel.currentUserId.collectAsStateWithLifecycle()
        val user by viewModel.userState.collectAsStateWithLifecycle()
        val portfolio by viewModel.portfolioState.collectAsStateWithLifecycle()
        val repos by viewModel.reposState.collectAsStateWithLifecycle()

        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          if (userId == null || user == null || portfolio == null) {
            LoginScreen(
              onConnectSuccess = { /* Session already active */ },
              onSyncTrigger = { username -> viewModel.syncGitHub(username) },
              modifier = Modifier.padding(innerPadding)
            )
          } else {
            DashboardScreen(
              user = user!!,
              portfolio = portfolio!!,
              repos = repos,
              onUpdateTagline = { tagline -> viewModel.updateTagline(tagline) },
              onUpdateTheme = { theme -> viewModel.updateTheme(theme) },
              onUpdatePublishStatus = { pub -> viewModel.updatePublishStatus(pub) },
              onToggleRepoVisibility = { repo -> viewModel.toggleRepoVisibility(repo) },
              onUpgradePro = { paymentId -> viewModel.upgradePro(paymentId) },
              onRefreshGitHub = { viewModel.refreshCurrentProfile() },
              onLogout = { viewModel.logout() },
              modifier = Modifier.padding(innerPadding)
            )
          }
        }
      }
    }
  }
}
