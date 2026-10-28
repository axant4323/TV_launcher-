package com.adithyatv.launcher.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.adithyatv.launcher.data.AppItem
import com.adithyatv.launcher.ui.theme.AccentAmber
import com.adithyatv.launcher.ui.theme.FocusBorderCyan
import com.adithyatv.launcher.ui.theme.TextPrimary

@Composable
fun AppCard(
    app: AppItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    cardWidth: Dp = 160.dp,
    cardHeight: Dp = 120.dp
) {
    val context = LocalContext.current

    // Safely load icon bitmap
    val iconBitmap = try {
        val pm = context.packageManager
        val drawable = pm.getApplicationIcon(app.packageName)
        drawable.toBitmap(width = 72, height = 72).asImageBitmap()
    } catch (_: Exception) {
        null
    }

    DpadFocusableCard(
        onClick = onClick,
        onLongClick = onLongClick,
        modifier = modifier
            .width(cardWidth)
            .height(cardHeight),
        contentDescriptionText = "${app.label}, ${if (app.isFavorite) "Favorite" else "App"}"
    ) { isFocused ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (iconBitmap != null) {
                    Image(
                        bitmap = iconBitmap,
                        contentDescription = null,
                        modifier = Modifier.size(54.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Tv,
                        contentDescription = null,
                        tint = if (isFocused) FocusBorderCyan else TextPrimary,
                        modifier = Modifier.size(54.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = app.label,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }

            // Favorite badge indicator
            if (app.isFavorite) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Favorite",
                    tint = AccentAmber,
                    modifier = Modifier
                        .size(18.dp)
                        .align(Alignment.TopEnd)
                )
            }
        }
    }
}
