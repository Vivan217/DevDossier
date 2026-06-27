package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.layout
import com.example.data.local.PortfolioEntity
import com.example.data.local.RepoEntity
import com.example.data.local.UserEntity
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import com.example.ui.components.AuroraBackground
import com.example.ui.components.GlassmorphicCard

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    user: UserEntity,
    portfolio: PortfolioEntity,
    repos: List<RepoEntity>,
    onUpdateTagline: (String) -> Unit,
    onUpdateTheme: (String) -> Unit,
    onUpdatePublishStatus: (Boolean) -> Unit,
    onToggleRepoVisibility: (RepoEntity) -> Unit,
    onUpgradePro: (paymentId: String) -> Unit,
    onRefreshGitHub: suspend () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    var taglineInput by remember(portfolio.tagline) { mutableStateOf(portfolio.tagline ?: "") }
    var isUpgrading by remember { mutableStateOf(false) }
    var isSyncing by remember { mutableStateOf(false) }

    val isPro = user.plan == "pro"

    AuroraBackground(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // --- 1. CONTROL HEADER BAR (Glassmorphic Pane) ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F172A).copy(alpha = 0.45f))
                        .border(
                            width = 1.dp,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.12f),
                                    Color.White.copy(alpha = 0.02f)
                                )
                            ),
                            shape = RoundedCornerShape(0.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Title and plan badge
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Dossier Editor",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(10.dp))

                            if (isPro) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            brush = Brush.linearGradient(listOf(EmeraldPro, PolishHighlightBg)),
                                            shape = RoundedCornerShape(50)
                                        )
                                        .padding(horizontal = 10.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "PRO",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PolishBackground,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .background(PolishSecondary, RoundedCornerShape(50))
                                        .clickable { isUpgrading = true }
                                        .padding(horizontal = 10.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Upgrade",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PolishPrimary,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        // Action controls: Refresh & Logout
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = {
                                    isSyncing = true
                                    coroutineScope.launch {
                                        try {
                                            onRefreshGitHub()
                                        } finally {
                                            isSyncing = false
                                        }
                                    }
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                if (isSyncing) {
                                    CircularProgressIndicator(color = PolishPrimary, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                } else {
                                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Sync Data", tint = PolishTextMuted)
                                }
                            }

                            IconButton(
                                onClick = onLogout,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Logout", tint = RoseCancel)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Settings controls row (Tagline, Theme, Publish Status)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Tagline Editor Field
                        OutlinedTextField(
                            value = taglineInput,
                            onValueChange = {
                                taglineInput = it
                                onUpdateTagline(it) // Instantly update view tagline
                            },
                            label = { Text("Profile Tagline", fontSize = 11.sp) },
                            placeholder = { Text("e.g. Lead Engineer", color = PolishTextNeutral) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PolishPrimary,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                                focusedTextColor = PolishTextPrimary,
                                unfocusedTextColor = PolishTextPrimary,
                                unfocusedLabelColor = PolishTextMuted,
                                focusedLabelColor = PolishPrimary
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                            modifier = Modifier
                                .weight(1.3f)
                                .height(48.dp),
                            textStyle = TextStyle(fontSize = 12.sp),
                            shape = RoundedCornerShape(8.dp)
                        )

                        // Theme selector button
                        var isThemeMenuExpanded by remember { mutableStateOf(false) }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .background(Color.White.copy(alpha = 0.06f), RoundedCornerShape(8.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                                .clickable { isThemeMenuExpanded = true }
                                .padding(horizontal = 10.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "THEME", fontSize = 8.sp, color = PolishTextMuted, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = portfolio.theme.replace("-", " ").uppercase(),
                                        fontSize = 11.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                }
                                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = PolishTextMuted)
                            }

                            DropdownMenu(
                                expanded = isThemeMenuExpanded,
                                onDismissRequest = { isThemeMenuExpanded = false },
                                modifier = Modifier.background(PolishSurface)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Modern Dark (Free)", color = Color.White, fontSize = 13.sp) },
                                    onClick = {
                                        onUpdateTheme("modern-dark")
                                        isThemeMenuExpanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("Midnight Neon", color = Color.White, fontSize = 13.sp)
                                            if (!isPro) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Icon(imageVector = Icons.Outlined.Lock, contentDescription = "Pro", tint = AmberStars, modifier = Modifier.size(12.dp))
                                            }
                                        }
                                    },
                                    onClick = {
                                        if (isPro) {
                                            onUpdateTheme("midnight-neon")
                                        } else {
                                            isUpgrading = true
                                        }
                                        isThemeMenuExpanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("Cosmic Slate", color = Color.White, fontSize = 13.sp)
                                            if (!isPro) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Icon(imageVector = Icons.Outlined.Lock, contentDescription = "Pro", tint = AmberStars, modifier = Modifier.size(12.dp))
                                            }
                                        }
                                    },
                                    onClick = {
                                        if (isPro) {
                                            onUpdateTheme("cosmic-slate")
                                        } else {
                                            isUpgrading = true
                                        }
                                        isThemeMenuExpanded = false
                                    }
                                )
                            }
                        }

                        // Publish State toggle
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .background(Color.White.copy(alpha = 0.06f), RoundedCornerShape(8.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                                .clickable { onUpdatePublishStatus(!portfolio.is_published) }
                                .padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "STATUS", fontSize = 8.sp, color = PolishTextMuted, fontWeight = FontWeight.Bold)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(
                                                color = if (portfolio.is_published) EmeraldPro else PolishTextMuted,
                                                shape = androidx.compose.foundation.shape.CircleShape
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (portfolio.is_published) "PUBLISHED" else "DRAFT",
                                        fontSize = 11.sp,
                                        color = if (portfolio.is_published) EmeraldPro else PolishTextMuted,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Switch(
                                checked = portfolio.is_published,
                                onCheckedChange = { onUpdatePublishStatus(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = EmeraldPro,
                                    checkedTrackColor = EmeraldPro.copy(alpha = 0.3f),
                                    uncheckedThumbColor = PolishOutline,
                                    uncheckedTrackColor = PolishSurface
                                ),
                                modifier = Modifier.scale(0.7f)
                            )
                        }
                    }
                }

                // --- 2. LIVE PREVIEW CONTAINER ---
                Box(modifier = Modifier.weight(1f)) {
                    // Background Preview Composable
                    PortfolioPreview(
                        user = user,
                        portfolio = portfolio,
                        repos = repos,
                        isEditable = true,
                        onToggleRepoVisibility = onToggleRepoVisibility
                    )

                    // Preview Badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 12.dp)
                            .background(PolishPrimary.copy(alpha = 0.15f), RoundedCornerShape(50))
                            .border(1.dp, PolishPrimary.copy(alpha = 0.3f), RoundedCornerShape(50))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "LIVE PREVIEW - TAP CARDS TO TOGGLE VISIBILITY",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = PolishPrimary,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            // --- 3. UPGRADE DIALOG MODAL ---
            if (isUpgrading) {
                UpgradeDialog(
                    onDismissRequest = { isUpgrading = false },
                    onUpgradeSuccess = { paymentId ->
                        onUpgradePro(paymentId)
                        isUpgrading = false
                    }
                )
            }
        }
    }
}
