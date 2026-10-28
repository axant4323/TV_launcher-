package com.adithyatv.launcher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adithyatv.launcher.ui.theme.DarkSurface
import com.adithyatv.launcher.ui.theme.FocusBorderCyan
import com.adithyatv.launcher.ui.theme.TextMuted
import com.adithyatv.launcher.ui.theme.TextPrimary
import com.adithyatv.launcher.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class GoogleTvTab {
    FOR_YOU,
    LIVE,
    APPS,
    LIBRARY,
    SEARCH,
    SETTINGS
}

@Composable
fun GoogleTvHeader(
    currentTab: GoogleTvTab,
    onTabSelected: (GoogleTvTab) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        while (true) {
            currentTime = format.format(Date())
            delay(1000)
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 36.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Google TV branding
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Google TV colored dots emblem
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(Color(0xFF4285F4)))
                Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(Color(0xFFEA4335)))
                Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(Color(0xFFFBBC05)))
                Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(Color(0xFF34A853)))
            }

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "Google",
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp,
                color = TextPrimary
            )
            Text(
                text = "tv",
                fontWeight = FontWeight.Medium,
                fontSize = 19.sp,
                color = Color(0xFF58A6FF)
            )
        }

        // Center: Primary Google TV Navigation Tabs
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GoogleTvTabPill(
                title = "For you",
                isSelected = currentTab == GoogleTvTab.FOR_YOU,
                onClick = { onTabSelected(GoogleTvTab.FOR_YOU) }
            )
            GoogleTvTabPill(
                title = "Live",
                isSelected = currentTab == GoogleTvTab.LIVE,
                onClick = { onTabSelected(GoogleTvTab.LIVE) }
            )
            GoogleTvTabPill(
                title = "Apps",
                isSelected = currentTab == GoogleTvTab.APPS,
                onClick = { onTabSelected(GoogleTvTab.APPS) }
            )
            GoogleTvTabPill(
                title = "Library",
                isSelected = currentTab == GoogleTvTab.LIBRARY,
                onClick = { onTabSelected(GoogleTvTab.LIBRARY) }
            )
            GoogleTvTabPill(
                title = "Search",
                icon = Icons.Default.Search,
                isSelected = currentTab == GoogleTvTab.SEARCH,
                onClick = { onTabSelected(GoogleTvTab.SEARCH) }
            )
        }

        // Right: Clock and Settings
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = currentTime,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            DpadFocusableCard(
                onClick = { onTabSelected(GoogleTvTab.SETTINGS) },
                shape = CircleShape,
                focusedBorderWidth = 2.dp,
                contentDescriptionText = "Settings"
            ) { isFocused ->
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(if (isFocused) FocusBorderCyan else DarkSurface, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = if (isFocused) Color.Black else TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun GoogleTvTabPill(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    DpadFocusableCard(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        focusedBorderWidth = 2.dp,
        focusedBorderColor = FocusBorderCyan,
        contentDescriptionText = title
    ) { isFocused ->
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(
                    when {
                        isSelected -> Color(0x33FFFFFF)
                        isFocused -> Color(0x44FFFFFF)
                        else -> Color.Transparent
                    }
                )
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected || isFocused) TextPrimary else TextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected || isFocused) TextPrimary else TextSecondary
            )
        }
    }
}
