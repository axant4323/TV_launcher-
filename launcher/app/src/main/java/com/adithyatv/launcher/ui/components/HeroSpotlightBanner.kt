package com.adithyatv.launcher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.adithyatv.launcher.data.MediaReleaseItem
import com.adithyatv.launcher.ui.theme.DarkBackground
import com.adithyatv.launcher.ui.theme.FocusBorderCyan
import com.adithyatv.launcher.ui.theme.TextMuted
import com.adithyatv.launcher.ui.theme.TextPrimary
import com.adithyatv.launcher.ui.theme.TextSecondary

@Composable
fun HeroSpotlightBanner(
    release: MediaReleaseItem,
    onWatchClick: (MediaReleaseItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(340.dp)
    ) {
        // 1. High-res backdrop image
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(release.backdropUrl)
                .crossfade(true)
                .build(),
            contentDescription = release.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Cinematic Gradient overlays (Google TV signature fade)
        // Left gradient for text readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            DarkBackground.copy(alpha = 0.95f),
                            DarkBackground.copy(alpha = 0.85f),
                            DarkBackground.copy(alpha = 0.4f),
                            Color.Transparent
                        ),
                        startX = 0f,
                        endX = 1200f
                    )
                )
        )

        // Bottom gradient fading into content rows
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            DarkBackground.copy(alpha = 0.5f),
                            DarkBackground
                        ),
                        startY = 150f,
                        endY = 340f
                    )
                )
        )

        // 3. Foreground Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            // Streaming Provider Badge Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(release.sourceColor))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = release.appSource.uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 0.8.sp,
                        color = Color.White
                    )
                }

                Text(
                    text = release.ratingBadge,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Title
            Text(
                text = release.title,
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Metadata: Year, Genre, Duration
            Text(
                text = "${release.releaseYear} • ${release.genre} • ${release.durationOrEpisode}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Synopsis
            Text(
                text = release.synopsis,
                style = MaterialTheme.typography.bodyLarge,
                color = TextMuted,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(0.65f)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                DpadFocusableCard(
                    onClick = { onWatchClick(release) },
                    shape = RoundedCornerShape(24.dp),
                    focusedBorderWidth = 2.dp,
                    focusedBorderColor = Color.White,
                    contentDescriptionText = "Watch ${release.title} on ${release.appSource}"
                ) { isFocused ->
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(if (isFocused) Color.White else Color(0x33FFFFFF))
                            .padding(horizontal = 22.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = if (isFocused) Color.Black else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Watch on ${release.appSource}",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = if (isFocused) Color.Black else Color.White
                        )
                    }
                }
            }
        }
    }
}
