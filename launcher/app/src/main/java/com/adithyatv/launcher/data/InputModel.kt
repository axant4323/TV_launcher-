package com.adithyatv.launcher.data

/**
 * Representation of a physical TV input source (HDMI, AV, etc.)
 */
data class InputItem(
    val id: String,
    val label: String,
    val type: InputType,
    val customName: String? = null,
    val intentUri: String? = null
) {
    val displayTitle: String
        get() = customName?.takeIf { it.isNotBlank() } ?: label
}

enum class InputType {
    HDMI_1,
    HDMI_2,
    HDMI_3,
    HDMI_4,
    AV,
    COMPONENT,
    TUNER,
    OTHER
}
