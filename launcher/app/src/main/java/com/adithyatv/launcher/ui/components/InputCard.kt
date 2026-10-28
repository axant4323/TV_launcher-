package com.adithyatv.launcher.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cable
import androidx.compose.material.icons.outlined.ConnectedTv
import androidx.compose.material.icons.outlined.Input
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.adithyatv.launcher.data.InputItem
import com.adithyatv.launcher.data.InputType
import com.adithyatv.launcher.ui.theme.AccentAmber
import com.adithyatv.launcher.ui.theme.FocusBorderCyan
import com.adithyatv.launcher.ui.theme.TextPrimary

@Composable
fun InputCard(
    input: InputItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cardWidth: Dp = 150.dp,
    cardHeight: Dp = 100.dp
) {
    val iconVector = when (input.type) {
        InputType.HDMI_1, InputType.HDMI_2, InputType.HDMI_3, InputType.HDMI_4 -> Icons.Outlined.Cable
        InputType.TUNER -> Icons.Outlined.Tv
        InputType.AV, InputType.COMPONENT -> Icons.Outlined.ConnectedTv
        else -> Icons.Outlined.Input
    }

    DpadFocusableCard(
        onClick = onClick,
        modifier = modifier
            .width(cardWidth)
            .height(cardHeight),
        contentDescriptionText = "Input ${input.displayTitle}"
    ) { isFocused ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = iconVector,
                contentDescription = null,
                tint = if (isFocused) FocusBorderCyan else AccentAmber,
                modifier = Modifier.size(36.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = input.displayTitle,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}
