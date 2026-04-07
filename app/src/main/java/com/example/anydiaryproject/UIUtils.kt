package com.example.anydiaryproject

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// =================== COLOR PALETTE ===================
// Primary – Muted Blue Highlight
val BlueSoft = Color(0xFFE6F0FF) // ฟ้าอ่อน
val BlueBright = Color(0xFF5581C3) // ฟ้าเข้ม

// Secondary – Soft Brown Accents
val BrownLight = Color(0xFFC8B6A6)
val BrownDark = Color(0xFF39231A) // Primary Button

// Background & Surface
val BgWarm = Color(0xFFFFFDF6)       // Warm cream
val CardWhite = Color(0xFFFFFFFF)
val FieldBg = Color(0xFFF5F2EC)       // Soft cream for input fields

// Text
val TextDark = Color(0xFF2A2A2A)
val TextGrey = Color(0xFF8C8C8C)
val TextLight = Color(0xFFCCCCCC)

// Status
val StatusRed = Color(0xFFFF98B9) // PinkAccent (ใช้แทน StatusRed เดิม)
val StatusGreen = Color(0xFF90C290)
val FavoriteStar = Color(0xFFFFD54F) // Gold star

// Expense Mode Colors
val ExpenseCardBg = Color(0xFFF8F6F2)      // Warm light gray
val ExpenseBadgeBg = Color(0xFFDCE8F8)     // Light blue badge
val ExpenseBadgeText = Color(0xFF5581C3)   // Blue text on badge
val PastelPeach = Color(0xFFFDE8DC)        // Peach circle bg
val PastelMint = Color(0xFFD4EDDA)         // Mint for income
val PastelLavender = Color(0xFFE8DEF8)     // Lavender accent
val PastelYellow = Color(0xFFFFF3CD)       // Yellow accent
val ExpenseAmountColor = Color(0xFF2D2016) // Dark brown for amount

// =================== SOFT DEPTH SHADOW ===================
fun Modifier.cardShadow(
    elevation: Dp = 8.dp,
    shape: Shape = RoundedCornerShape(20.dp)
): Modifier = this.shadow(
    elevation = elevation,
    shape = shape,
    ambientColor = BrownDark.copy(alpha = 0.05f),
    spotColor = BrownDark.copy(alpha = 0.08f)
)

// =================== BOUNCY CLICK ===================
fun Modifier.bouncyClick(onClick: () -> Unit): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(durationMillis = 80),
        label = "bounce"
    )
    this
        .scale(scale)
        .clickable(
            interactionSource = interactionSource,
            indication = null as androidx.compose.foundation.Indication?,
            onClick = onClick
        )
}

@Composable
fun AppLogo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_any_new),
            contentDescription = "ANY Diary",
            modifier = Modifier
                .size(56.dp)
                .cardShadow(12.dp, CircleShape)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}
