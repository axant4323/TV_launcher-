package com.adithyatv.launcher.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Wifi
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.adithyatv.launcher.ui.theme.AccentAmber
import com.adithyatv.launcher.ui.theme.FocusBorderCyan
import com.adithyatv.launcher.ui.theme.TextMuted
import com.adithyatv.launcher.ui.theme.TextPrimary
import com.adithyatv.launcher.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class LauncherScreen {
    HOME,
    ALL_APPS,
    INPUTS,
    SEARCH,
    SETTINGS
}

@Composable
fun TopStatusBar(
    currentScreen: LauncherScreen,
    onNavigate: (LauncherScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }

    // Real-time clock update loop
    LaunchedEffect(Unit) {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
        while (true) {
            val now = Date()
            currentTime = timeFormat.format(now)
            currentDate = dateFormat.format(now)
            delay(1000)
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Navigation pills
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavButton(
                title = "Home",
                icon = Icons.Default.Home,
                isSelected = currentScreen == LauncherScreen.HOME,
                onClick = { onNavigate(LauncherScreen.HOME) }
            )
            NavButton(
                title = "All Apps",
                icon = Icons.Default.Apps,
                isSelected = currentScreen == LauncherScreen.ALL_APPS,
                onClick = { onNavigate(LauncherScreen.ALL_APPS) }
            )
            NavButton(
                title = "Inputs",
                icon = Icons.Default.Tv,
                isSelected = currentScreen == LauncherScreen.INPUTS,
                onClick = { onNavigate(LauncherScreen.INPUTS) }
            )
            NavButton(
                title = "Search",
                icon = Icons.Default.Search,
                isSelected = currentScreen == LauncherScreen.SEARCH,
                onClick = { onNavigate(LauncherScreen.SEARCH) }
            )
            NavButton(
                title = "Settings",
                icon = Icons.Default.Settings,
                isSelected = currentScreen == LauncherScreen.SETTINGS,
                onClick = { onNavigate(LauncherScreen.SETTINGS) }
            )
        }

        // Time, Date & Connectivity Indicator
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Icon(
                imageVector = Icons.Default.Wifi,
                contentDescription = "Connected",
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = currentDate,
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = currentTime,
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun NavButton(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    DpadFocusableCard(
        onClick = onClick,
        focusedBorderColor = FocusBorderCyan,
        focusedBorderWidth = 2.dp,
        contentDescriptionText = title
    ) { isFocused ->
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isFocused || isSelected) FocusBorderCyan else TextSecondary,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isFocused || isSelected) TextPrimary else TextSecondary
            )
        }
    }
}
