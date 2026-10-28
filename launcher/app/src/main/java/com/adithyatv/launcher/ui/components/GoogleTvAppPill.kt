package com.adithyatv.launcher.ui.components

import android.view.KeyEvent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.adithyatv.launcher.data.AppItem
import com.adithyatv.launcher.ui.theme.DarkBorder
import com.adithyatv.launcher.ui.theme.DarkSurface
import com.adithyatv.launcher.ui.theme.DarkSurfaceFocused
import com.adithyatv.launcher.ui.theme.FocusBorderCyan
import com.adithyatv.launcher.ui.theme.TextMuted
import com.adithyatv.launcher.ui.theme.TextPrimary

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GoogleTvAppPill(
    app: AppItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    var isLongPressHandled by remember { mutableStateOf(false) }
    var keyDownTimestamp by remember { mutableLongStateOf(0L) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.12f else 1.0f,
        animationSpec = tween(durationMillis = 150),
        label = "pill_scale"
    )

    val iconBitmap = remember(app.packageName) {
        try {
            val pm = context.packageManager
            val drawable = pm.getApplicationIcon(app.packageName)
            drawable.toBitmap(width = 80, height = 80).asImageBitmap()
        } catch (_: Exception) {
            null
        }
    }

    val shape = RoundedCornerShape(18.dp)

    Column(
        modifier = modifier
            .width(88.dp)
            .scale(scale)
            .semantics { contentDescription = "${app.label} app" },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .shadow(if (isFocused) 14.dp else 2.dp, shape)
                .clip(shape)
                .background(if (isFocused) DarkSurfaceFocused else DarkSurface)
                .border(
                    BorderStroke(
                        width = if (isFocused) 3.dp else 1.dp,
                        color = if (isFocused) Color.White else DarkBorder
                    ),
                    shape
                )
                .focusable(interactionSource = interactionSource)
                .onKeyEvent { keyEvent ->
                    val native = keyEvent.nativeKeyEvent
                    val isCenter = native.keyCode == KeyEvent.KEYCODE_DPAD_CENTER || native.keyCode == KeyEvent.KEYCODE_ENTER
                    if (isCenter) {
                        if (native.action == KeyEvent.ACTION_DOWN) {
                            if (native.repeatCount == 0) {
                                keyDownTimestamp = System.currentTimeMillis()
                                isLongPressHandled = false
                            } else if (!isLongPressHandled && onLongClick != null) {
                                val elapsed = System.currentTimeMillis() - keyDownTimestamp
                                if (native.repeatCount >= 2 || elapsed >= 450) {
                                    isLongPressHandled = true
                                    onLongClick()
                                    return@onKeyEvent true
                                }
                            }
                        } else if (native.action == KeyEvent.ACTION_UP) {
                            if (isLongPressHandled) {
                                isLongPressHandled = false
                                return@onKeyEvent true
                            } else {
                                onClick()
                                return@onKeyEvent true
                            }
                        }
                    }
                    false
                }
                .combinedClickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                    onLongClick = onLongClick
                ),
            contentAlignment = Alignment.Center
        ) {
            if (iconBitmap != null) {
                Image(
                    bitmap = iconBitmap,
                    contentDescription = null,
                    modifier = Modifier.size(52.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.Tv,
                    contentDescription = null,
                    tint = if (isFocused) FocusBorderCyan else TextPrimary,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = app.label,
            fontSize = 12.sp,
            fontWeight = if (isFocused) FontWeight.SemiBold else FontWeight.Medium,
            color = if (isFocused) TextPrimary else TextMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}
