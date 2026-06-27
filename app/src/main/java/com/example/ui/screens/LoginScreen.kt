package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.CallReceived
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.FolderShared
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import com.example.ui.components.AuroraBackground
import com.example.ui.components.GlassmorphicCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onConnectSuccess: (userId: String) -> Unit,
    onSyncTrigger: suspend (username: String) -> Result<String>,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    var username by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AuroraBackground(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // --- LOGO / HEADER ---
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            color = PolishPrimary,
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Code,
                        contentDescription = "DevDossier Logo",
                        tint = PolishOnPrimary,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "DevDossier",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = PolishTextPrimary,
                    letterSpacing = (-1).sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Your GitHub portfolio, generated instantly",
                    fontSize = 15.sp,
                    color = PolishTextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(40.dp))

                // --- INPUT CARD ---
                GlassmorphicCard(
                    cornerRadius = 16.dp,
                    bgAlpha = 0.42f,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Connect your GitHub account",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PolishTextPrimary,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Enter your username to pull your profile, repositories, stars and top languages.",
                            fontSize = 12.sp,
                            color = PolishTextMuted,
                            modifier = Modifier.align(Alignment.Start),
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // TextField
                        OutlinedTextField(
                            value = username,
                            onValueChange = {
                                username = it
                                errorMessage = null
                            },
                            label = { Text("GitHub Username", color = PolishTextMuted) },
                            placeholder = { Text("e.g. torvalds", color = PolishTextNeutral) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PolishPrimary,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                                focusedTextColor = PolishTextPrimary,
                                unfocusedTextColor = PolishTextPrimary,
                                cursorColor = PolishPrimary
                            ),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { keyboardController?.hide() }
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Action Button
                        Button(
                            onClick = {
                                if (username.isBlank()) {
                                    errorMessage = "Username cannot be empty"
                                    return@Button
                                }
                                keyboardController?.hide()
                                isLoading = true
                                errorMessage = null

                                // Trigger sync
                                coroutineScope.launch {
                                    val result = onSyncTrigger(username)
                                    isLoading = false
                                    if (result.isSuccess) {
                                        onConnectSuccess(result.getOrThrow())
                                    } else {
                                        errorMessage = result.exceptionOrNull()?.message ?: "Unable to sync profile. Check your connection or username."
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PolishPrimary,
                                contentColor = PolishOnPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = PolishOnPrimary,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.5.dp
                                )
                            } else {
                                Text(
                                    text = "Auto-Generate Portfolio",
                                    color = PolishOnPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }

                        // Error Message
                        AnimatedVisibility(
                            visible = errorMessage != null,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            errorMessage?.let { msg ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp)
                                        .background(RoseCancel.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                        .border(1.dp, RoseCancel.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Warning,
                                        contentDescription = "Warning",
                                        tint = RoseCancel,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = msg,
                                        color = RoseCancel,
                                        fontSize = 12.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // --- ONBOARDING FEATURES ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FeatureBox(
                        icon = Icons.Outlined.FolderShared,
                        title = "Interactive Cache",
                        desc = "Your data is cached locally for instantaneous loading.",
                        modifier = Modifier.weight(1f)
                    )

                    FeatureBox(
                        icon = Icons.Outlined.Link,
                        title = "Slug Router",
                        desc = "Direct portfolio links map to unique custom slugs.",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun FeatureBox(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    desc: String,
    modifier: Modifier = Modifier
) {
    GlassmorphicCard(
        cornerRadius = 12.dp,
        bgAlpha = 0.32f,
        borderAlpha = 0.12f,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = PolishPrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = PolishTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = desc,
                fontSize = 11.sp,
                color = PolishTextMuted,
                lineHeight = 15.sp
            )
        }
    }
}
