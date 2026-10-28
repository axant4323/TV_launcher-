package com.adithyatv.launcher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ZoomIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adithyatv.launcher.ui.components.DpadFocusableCard
import com.adithyatv.launcher.ui.theme.DarkBorder
import com.adithyatv.launcher.ui.theme.DarkSurface
import com.adithyatv.launcher.ui.theme.FocusBorderCyan
import com.adithyatv.launcher.ui.theme.SuccessGreen
import com.adithyatv.launcher.ui.theme.TextMuted
import com.adithyatv.launcher.ui.theme.TextPrimary
import com.adithyatv.launcher.ui.theme.TextSecondary

@Composable
fun SettingsScreen(
    currentUiScale: Float,
    onSetUiScale: (Float) -> Unit,
    onOpenSystemSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Launcher Settings & Recovery Guidance",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(18.dp))

        // 1. Android TV System Settings Shortcut
        Text(
            text = "Android System Settings",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        DpadFocusableCard(
            onClick = onOpenSystemSettings,
            modifier = Modifier.fillMaxWidth().height(80.dp)
        ) { isFocused ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = null,
                    tint = if (isFocused) FocusBorderCyan else TextPrimary,
                    modifier = Modifier.size(32.dp)
                )
                Column {
                    Text(
                        text = "Open Native Android TV Settings",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isFocused) TextPrimary else TextPrimary
                    )
                    Text(
                        text = "Access Wi-Fi, Bluetooth remotes, Display, Audio, Inputs, and System Preferences",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. UI Scale Selector
        Text(
            text = "10-Foot UI Scaling (Legibility)",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ScaleOptionCard(
                title = "Compact (0.85x)",
                scale = 0.85f,
                isSelected = currentUiScale == 0.85f,
                onClick = { onSetUiScale(0.85f) },
                modifier = Modifier.weight(1f)
            )
            ScaleOptionCard(
                title = "Standard (1.0x)",
                scale = 1.0f,
                isSelected = currentUiScale == 1.0f,
                onClick = { onSetUiScale(1.0f) },
                modifier = Modifier.weight(1f)
            )
            ScaleOptionCard(
                title = "Large / 10-Foot (1.15x)",
                scale = 1.15f,
                isSelected = currentUiScale == 1.15f,
                onClick = { onSetUiScale(1.15f) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3. Reversible Setup & Recovery Guidance
        Text(
            text = "Reversible Setup & Safe Fallback",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface, RoundedCornerShape(12.dp))
                .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Security,
                    contentDescription = null,
                    tint = SuccessGreen,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Zero-Risk Architecture",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
            }
            Text(
                text = "• The original stock launcher is NOT uninstalled from the system partition.\n" +
                       "• You can return to the factory launcher anytime via TV Settings > Apps > Special App Access > Default Home App.\n" +
                       "• Emergency ADB fallback: `adb shell cmd package set-home-activity com.google.android.tvlauncher/.MainActivity` (or original launcher component).\n" +
                       "• Zero tracking, zero telemetry, zero analytics, zero internet permissions required.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun ScaleOptionCard(
    title: String,
    scale: Float,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DpadFocusableCard(
        onClick = onClick,
        modifier = modifier.height(68.dp),
        contentDescriptionText = "$title ${if (isSelected) "selected" else ""}"
    ) { isFocused ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = FocusBorderCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = if (isFocused || isSelected) TextPrimary else TextSecondary
            )
        }
    }
}
