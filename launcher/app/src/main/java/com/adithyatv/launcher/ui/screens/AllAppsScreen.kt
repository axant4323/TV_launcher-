package com.adithyatv.launcher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adithyatv.launcher.data.AppItem
import com.adithyatv.launcher.ui.components.AppCard
import com.adithyatv.launcher.ui.components.DpadFocusableCard
import com.adithyatv.launcher.ui.theme.AccentAmber
import com.adithyatv.launcher.ui.theme.DarkBorder
import com.adithyatv.launcher.ui.theme.DarkSurface
import com.adithyatv.launcher.ui.theme.FocusBorderCyan
import com.adithyatv.launcher.ui.theme.TextMuted
import com.adithyatv.launcher.ui.theme.TextPrimary
import com.adithyatv.launcher.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllAppsScreen(
    allApps: List<AppItem>,
    onLaunchApp: (AppItem) -> Unit,
    onToggleFavorite: (AppItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedAppForDialog by remember { mutableStateOf<AppItem?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 36.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "All Applications (${allApps.size})",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary
            )
            Text(
                text = "Center: Open or Favorite • Hold Center: Quick Favorite ★",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (allApps.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No launchable applications detected.",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextSecondary
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(allApps, key = { it.packageName }) { app ->
                    AppCard(
                        app = app,
                        onClick = {
                            selectedAppForDialog = app
                        },
                        onLongClick = {
                            onToggleFavorite(app)
                        }
                    )
                }
            }
        }
    }

    // App Action Dialog (D-pad accessible on every TV remote)
    selectedAppForDialog?.let { app ->
        BasicAlertDialog(
            onDismissRequest = { selectedAppForDialog = null }
        ) {
            Column(
                modifier = Modifier
                    .width(360.dp)
                    .background(DarkSurface, RoundedCornerShape(16.dp))
                    .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = app.label,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = TextPrimary
                )
                Text(
                    text = app.packageName,
                    fontSize = 12.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Option 1: Open App
                DpadFocusableCard(
                    onClick = {
                        selectedAppForDialog = null
                        onLaunchApp(app)
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) { isFocused ->
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = if (isFocused) FocusBorderCyan else TextPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "Open Application",
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                }

                // Option 2: Toggle Favorite
                DpadFocusableCard(
                    onClick = {
                        val currentApp = app
                        selectedAppForDialog = null
                        onToggleFavorite(currentApp)
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) { isFocused ->
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = if (app.isFavorite) Icons.Default.Star else Icons.Outlined.StarOutline,
                            contentDescription = null,
                            tint = if (app.isFavorite) AccentAmber else (if (isFocused) FocusBorderCyan else TextPrimary),
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = if (app.isFavorite) "Remove from Favorites" else "★ Add to Favorites",
                            fontWeight = FontWeight.SemiBold,
                            color = if (app.isFavorite) AccentAmber else TextPrimary
                        )
                    }
                }

                // Option 3: Cancel
                DpadFocusableCard(
                    onClick = { selectedAppForDialog = null },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) { isFocused ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Cancel",
                            fontWeight = FontWeight.Medium,
                            color = if (isFocused) Color.White else TextMuted
                        )
                    }
                }
            }
        }
    }
}
