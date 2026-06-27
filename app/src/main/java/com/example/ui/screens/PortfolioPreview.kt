package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CallReceived
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Launch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.PortfolioEntity
import com.example.data.local.RepoEntity
import com.example.data.local.UserEntity
import com.example.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PortfolioPreview(
    user: UserEntity,
    portfolio: PortfolioEntity,
    repos: List<RepoEntity>,
    modifier: Modifier = Modifier,
    isEditable: Boolean = false,
    onToggleRepoVisibility: ((RepoEntity) -> Unit)? = null
) {
    val context = LocalContext.current
    val currentTheme = portfolio.theme

    // Resolve colors based on chosen theme
    val (themeBg, themeCardBg, themeAccent, themeText, themeSubtext) = when (currentTheme) {
        "midnight-neon" -> listOf(
            Color(0xFF03001C), // Deepest black-indigo
            Color(0xFF1B1035), // Glowing purple-black card
            VioletTheme,       // Radiant violet
            Color.White,
            Color(0xFFC084FC)  // Soft purple
        )
        "cosmic-slate" -> listOf(
            Color(0xFF111827), // Deep space grey
            Color(0xFF1F2937), // Space card grey
            AmberStars,        // Cosmic gold
            Color.White,
            Color(0xFFF3F4F6)
        )
        else -> listOf( // "modern-dark" (default)
            PolishBackground,
            PolishSurface,
            PolishPrimary,
            PolishTextPrimary,
            PolishTextMuted
        )
    }

    val animatedAccent by animateColorAsState(targetValue = themeAccent, label = "ThemeAccent")

    com.example.ui.components.AuroraBackground(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 24.dp)
        ) {
        // --- 1. HERO SECTION ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar with glowing border
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .drawBehind {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        animatedAccent.copy(alpha = 0.4f),
                                        Color.Transparent
                                    )
                                ),
                                radius = size.minDimension * 0.7f
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = user.avatar_url,
                        contentDescription = "Avatar of ${user.display_name}",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(2.dp, animatedAccent, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Display Name
                Text(
                    text = user.display_name ?: user.github_username,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = themeText,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Tagline
                Text(
                    text = portfolio.tagline ?: "Full-stack Developer",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Monospace,
                    color = animatedAccent,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Bio
                Text(
                    text = user.bio ?: "No bio provided.",
                    fontSize = 14.sp,
                    color = themeSubtext,
                    textAlign = TextAlign.Center,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Contact/GitHub Button
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(repos.firstOrNull()?.url ?: "https://github.com/${user.github_username}"))
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.08f)),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, animatedAccent.copy(alpha = 0.4f)),
                    modifier = Modifier.height(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Launch,
                        contentDescription = "GitHub Icon",
                        tint = animatedAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "@${user.github_username}",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // --- 2. STATS STRIP ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(Color.White.copy(alpha = 0.04f), RoundedCornerShape(12.dp))
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.14f),
                            Color.White.copy(alpha = 0.03f)
                        )
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Stat 1: Projects Count
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "PROJECTS",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = animatedAccent,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${repos.size}",
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Stat 2: Total Stars
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "STARS",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = animatedAccent,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${repos.sumOf { it.stars }}",
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Stat 3: Top Language
            val topLanguage = repos.groupBy { it.language }
                .maxByOrNull { it.value.size }?.key ?: "Kotlin"
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "TOP TECH",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = animatedAccent,
                    letterSpacing = 1.sp
                )
                Text(
                    text = topLanguage,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- 3. PROJECT GRID ---
        Text(
            text = "Featured Projects",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = themeText,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )

        val visibleRepos = if (isEditable) repos else repos.filter { it.is_visible }

        if (visibleRepos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No visible projects. Click repositories in editor below to toggle visibility.",
                    color = PolishTextMuted,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(visibleRepos) { repo ->
                    val isHidden = isEditable && !repo.is_visible
                    val cardAlpha = if (isHidden) 0.15f else 0.45f
                    val borderAlpha = if (isHidden) 0.04f else 0.15f

                    com.example.ui.components.GlassmorphicCard(
                        cornerRadius = 12.dp,
                        bgAlpha = cardAlpha,
                        borderAlpha = borderAlpha,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clickable {
                                if (isEditable && onToggleRepoVisibility != null) {
                                    onToggleRepoVisibility(repo)
                                } else {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(repo.url))
                                    context.startActivity(intent)
                                }
                            }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Language Badge
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = animatedAccent.copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = repo.language ?: "Code",
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = animatedAccent
                                        )
                                    }

                                    // Star count
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.Star,
                                            contentDescription = "Stars",
                                            tint = AmberStars,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = "${repo.stars}",
                                            fontSize = 11.sp,
                                            color = Color.White,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Repo Name
                                Text(
                                    text = repo.repo_name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                // Description
                                Text(
                                    text = repo.description ?: "No description provided.",
                                    fontSize = 11.sp,
                                    color = PolishTextMuted,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    lineHeight = 14.sp
                                )
                            }

                            if (isEditable) {
                                Text(
                                    text = if (repo.is_visible) "✓ Visible" else "✗ Hidden",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (repo.is_visible) EmeraldPro else RoseCancel,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.align(Alignment.End)
                                )
                            } else {
                                Row(
                                    modifier = Modifier.align(Alignment.End),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "View",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = animatedAccent
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Icon(
                                        imageVector = Icons.Outlined.Launch,
                                        contentDescription = "Launch",
                                        tint = animatedAccent,
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 4. ORGANIC MARKETING FOOTER ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "© ${java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)} ${user.display_name ?: user.github_username}",
                    fontSize = 11.sp,
                    color = PolishTextMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(50))
                        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(50))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Built with ",
                        fontSize = 10.sp,
                        color = PolishTextMuted
                    )
                    Text(
                        text = "DevDossier",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
}
