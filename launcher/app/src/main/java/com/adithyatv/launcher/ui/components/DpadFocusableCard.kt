package com.adithyatv.launcher.ui.components

import android.view.KeyEvent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.adithyatv.launcher.ui.theme.DarkBorder
import com.adithyatv.launcher.ui.theme.DarkSurface
import com.adithyatv.launcher.ui.theme.DarkSurfaceFocused
import com.adithyatv.launcher.ui.theme.FocusBorderCyan

/**
 * TV D-pad focusable surface card with oversized 10-foot legibility,
 * distinct focus scaling (1.05x), cyan focus outline, and native
 * physical remote D-pad Center long-press detection.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DpadFocusableCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    shape: Shape = RoundedCornerShape(12.dp),
    focusedBorderColor: Color = FocusBorderCyan,
    unfocusedBorderColor: Color = DarkBorder,
    focusedBorderWidth: Dp = 3.dp,
    unfocusedBorderWidth: Dp = 1.dp,
    contentDescriptionText: String? = null,
    content: @Composable BoxScope.(isFocused: Boolean) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    var isLongPressHandled by remember { mutableStateOf(false) }
    var keyDownTimestamp by remember { mutableLongStateOf(0L) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.05f else 1.0f,
        animationSpec = tween(durationMillis = 150),
        label = "card_scale"
    )

    val elevation = if (isFocused) 16.dp else 2.dp
    val backgroundColor = if (isFocused) DarkSurfaceFocused else DarkSurface
    val borderStroke = BorderStroke(
        width = if (isFocused) focusedBorderWidth else unfocusedBorderWidth,
        color = if (isFocused) focusedBorderColor else unfocusedBorderColor
    )

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(elevation, shape)
            .background(backgroundColor, shape)
            .border(borderStroke, shape)
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
            )
            .semantics {
                if (contentDescriptionText != null) {
                    contentDescription = contentDescriptionText
                }
            }
    ) {
        content(isFocused)
    }
}
