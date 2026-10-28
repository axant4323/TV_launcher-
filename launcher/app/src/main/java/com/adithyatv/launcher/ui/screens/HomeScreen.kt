package com.adithyatv.launcher.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adithyatv.launcher.data.AppItem
import com.adithyatv.launcher.data.InputItem
import com.adithyatv.launcher.ui.components.AppCard
import com.adithyatv.launcher.ui.components.DpadFocusableCard
import com.adithyatv.launcher.ui.components.InputCard
import com.adithyatv.launcher.ui.components.LauncherScreen
import com.adithyatv.launcher.ui.theme.FocusBorderCyan
import com.adithyatv.launcher.ui.theme.TextMuted
import com.adithyatv.launcher.ui.theme.TextPrimary
import com.adithyatv.launcher.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    favoriteApps: List<AppItem>,
    tvInputs: List<InputItem>,
    onLaunchApp: (AppItem) -> Unit,
    onToggleFavorite: (AppItem) -> Unit,
    onSelectInput: (InputItem) -> Unit,
    onNavigate: (LauncherScreen) -> Unit,
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
        // --- 1. Favorites Row ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "Favorite Apps",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary
            )
            Text(
                text = "Press Center to open • Long-press to remove",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (favoriteApps.isEmpty()) {
            DpadFocusableCard(
                onClick = { onNavigate(LauncherScreen.ALL_APPS) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
            ) { isFocused ->
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = null,
                        tint = if (isFocused) FocusBorderCyan else TextSecondary
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(
                        text = "No favorites set yet. Click here to select favorite apps from All Apps",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isFocused) TextPrimary else TextSecondary
                    )
                }
            }
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
            ) {
                items(favoriteApps, key = { it.packageName }) { app ->
                    AppCard(
                        app = app,
                        onClick = { onLaunchApp(app) },
                        onLongClick = { onToggleFavorite(app) }
                    )
                }

                // Quick Add shortcut at the end of the row
                item {
                    DpadFocusableCard(
                        onClick = { onNavigate(LauncherScreen.ALL_APPS) },
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .height(120.dp)
                    ) { isFocused ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Add,
                                contentDescription = null,
                                tint = if (isFocused) FocusBorderCyan else TextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "All Apps",
                                style = MaterialTheme.typography.titleMedium,
                                color = if (isFocused) TextPrimary else TextSecondary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        // --- 2. TV Inputs Row ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "Inputs & Sources",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary
            )
            Text(
                text = "Switch HDMI / Cable / AV sources",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
        ) {
            items(tvInputs, key = { it.id }) { input ->
                InputCard(
                    input = input,
                    onClick = { onSelectInput(input) }
                )
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        // --- 3. Quick System Access Row ---
        Text(
            text = "Quick Tools",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DpadFocusableCard(
                onClick = onOpenSystemSettings,
                modifier = Modifier.height(72.dp)
            ) { isFocused ->
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = null,
                        tint = if (isFocused) FocusBorderCyan else TextSecondary
                    )
                    Text(
                        text = "Android TV System Settings",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isFocused) TextPrimary else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
