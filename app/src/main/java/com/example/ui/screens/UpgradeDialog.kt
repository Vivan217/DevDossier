package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import java.util.UUID
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun UpgradeDialog(
    onDismissRequest: () -> Unit,
    onUpgradeSuccess: (paymentId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var checkoutStep by remember { mutableStateOf("modal") } // "modal", "razorpay_checkout", "processing", "success"
    var testPaymentId by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { if (checkoutStep != "processing") onDismissRequest() }) {
        com.example.ui.components.GlassmorphicCard(
            cornerRadius = 20.dp,
            bgAlpha = 0.65f,
            borderAlpha = 0.15f,
            modifier = modifier.fillMaxWidth()
        ) {
            when (checkoutStep) {
                "modal" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Title
                        Text(
                            text = "Upgrade to Pro",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Unlock developer superpowers",
                            fontSize = 13.sp,
                            color = PolishTextMuted,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Features list
                        FeatureRow(title = "Auto-generated portfolio site", free = true, pro = true)
                        FeatureRow(title = "Featured repository selection", free = true, pro = true)
                        FeatureRow(title = "Premium dark cyber themes", free = false, pro = true)
                        FeatureRow(title = "Custom Domain mappings", free = false, pro = true)
                        FeatureRow(title = "Unlimited synced repositories", free = false, pro = true)

                        Spacer(modifier = Modifier.height(24.dp))

                        // Price
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(PolishPrimary.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                .border(1.dp, PolishPrimary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "DevDossier Pro Lifetime",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PolishPrimary,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "₹499",
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = " / one-time",
                                        fontSize = 14.sp,
                                        color = PolishTextMuted
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Buttons
                        Button(
                            onClick = { checkoutStep = "razorpay_checkout" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PolishPrimary,
                                contentColor = PolishOnPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Unlock Pro Instantly",
                                color = PolishOnPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(onClick = onDismissRequest) {
                            Text(text = "Keep Free Plan", color = PolishTextMuted, fontSize = 13.sp)
                        }
                    }
                }

                "razorpay_checkout" -> {
                    // Simulated Razorpay Pop-up UI (Secure payment)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Razorpay Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "RAZORPAY SECURE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0B65C6),
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "DevDossier Pro Plan Upgrade",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Text(
                                text = "₹499.00",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Divider(modifier = Modifier.padding(vertical = 16.dp), color = PolishOutline)

                        Text(
                            text = "[TEST MODE - Razorpay Sandbox]",
                            color = AmberStars,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Icon(
                            imageVector = Icons.Outlined.CreditCard,
                            contentDescription = "Card",
                            tint = PolishPrimary,
                            modifier = Modifier.size(64.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Simulating server-side Razorpay webhook validation. Once clicked, Razorpay will issue a webhook with sign signature verification (HMAC SHA256) inside our secure server function to unlock Pro features. This guarantees no client-side faking is possible.",
                            fontSize = 12.sp,
                            color = PolishTextMuted,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { checkoutStep = "modal" },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                border = BorderStroke(1.dp, PolishOutline)
                            ) {
                                Text(text = "Cancel")
                            }

                            Button(
                                onClick = {
                                    checkoutStep = "processing"
                                    testPaymentId = "pay_" + UUID.randomUUID().toString().replace("-", "").take(14)
                                    coroutineScope.launch {
                                        // Simulate secure server-side Webhook verification
                                        delay(3000)
                                        checkoutStep = "success"
                                        delay(1500)
                                        onUpgradeSuccess(testPaymentId)
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPro)
                            ) {
                                Text(text = "Simulate Pay", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                "processing" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = PolishPrimary, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Verifying signature...",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Awaiting secure webhook confirmation from Razorpay sandbox server to flip plan: pro.",
                            fontSize = 12.sp,
                            color = PolishTextMuted,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )
                    }
                }

                "success" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(EmeraldPro.copy(alpha = 0.15f), CircleShape)
                                .border(2.dp, EmeraldPro, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Check",
                                tint = EmeraldPro,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Upgrade Verified!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Your account has been promoted to DevDossier Pro. Premium features unlocked.",
                            fontSize = 12.sp,
                            color = PolishTextMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FeatureRow(
    title: String,
    free: Boolean,
    pro: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, color = PolishTextPrimary, fontSize = 13.sp, modifier = Modifier.weight(1f))
        
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            // Free Column
            if (free) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Yes",
                    tint = EmeraldPro,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "No",
                    tint = RoseCancel,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Pro Column
            if (pro) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Yes",
                    tint = EmeraldPro,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Locked",
                    tint = PolishTextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
