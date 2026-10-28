package com.adithyatv.launcher.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.adithyatv.launcher.data.MediaReleaseItem
import com.adithyatv.launcher.ui.theme.DarkBorder
import com.adithyatv.launcher.ui.theme.DarkSurface
import com.adithyatv.launcher.ui.theme.FocusBorderCyan
import com.adithyatv.launcher.ui.theme.TextMuted
import com.adithyatv.launcher.ui.theme.TextPrimary

@Composable
fun MediaReleaseCard(
    release: MediaReleaseItem,
    onClick: () -> Unit,
    onFocused: (MediaReleaseItem) -> Unit,
    modifier: Modifier = Modifier,
    cardWidth: Dp = 230.dp,
    cardHeight: Dp = 135.dp
) {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    LaunchedEffect(isFocused) {
        if (isFocused) {
            onFocused(release)
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.08f else 1.0f,
        animationSpec = tween(durationMillis = 150),
        label = "media_card_scale"
    )

    val shape = RoundedCornerShape(10.dp)

    Box(
        modifier = modifier
            .width(cardWidth)
            .height(cardHeight)
            .scale(scale)
            .shadow(if (isFocused) 16.dp else 2.dp, shape)
            .clip(shape)
            .background(DarkSurface)
            .border(
                BorderStroke(
                    width = if (isFocused) 3.dp else 1.dp,
                    color = if (isFocused) Color.White else DarkBorder
                ),
                shape
            )
            .focusable(interactionSource = interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .semantics {
                contentDescription = "${release.title} on ${release.appSource}"
            }
    ) {
        // Thumbnail image
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(release.posterUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient for text legibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xCC000000),
                            Color(0xEE000000)
                        ),
                        startY = 60f
                    )
                )
        )

        // Source badge (top-left)
        Box(
            modifier = Modifier
                .padding(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(release.sourceColor))
                .padding(horizontal = 6.dp, vertical = 2.dp)
                .align(Alignment.TopStart)
        ) {
            Text(
                text = release.appSource,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Title and metadata (bottom)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(10.dp)
        ) {
            Text(
                text = release.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${release.genre} • ${release.releaseYear}",
                fontSize = 11.sp,
                color = TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
