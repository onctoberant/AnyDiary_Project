package com.example.anydiaryproject

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrownDark),
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_any_new),
            contentDescription = "Logo",
            modifier = Modifier
                .width(300.dp)
                .align(Alignment.Center),
            contentScale = ContentScale.Fit
        )
        
        IconButton(
            onClick = onTimeout,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp)
                .size(64.dp)
                .border(1.dp, CardWhite, CircleShape)
        ) {
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = "Start",
                tint = CardWhite,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
