package com.adithyatv.launcher.data

import android.content.Context
import android.content.Intent
import android.media.tv.TvContract
import android.media.tv.TvInputInfo
import android.media.tv.TvInputManager
import android.net.Uri
import android.util.Log

class InputRepository(private val context: Context) {
    private val tag = "InputRepository"

    /**
     * Discovers hardware TV inputs using TvInputManager, with safe fallback.
     */
    fun getTvInputs(): List<InputItem> {
        val list = mutableListOf<InputItem>()

        try {
            val tvInputManager = context.getSystemService(Context.TV_INPUT_SERVICE) as? TvInputManager
            if (tvInputManager != null) {
                val inputList = tvInputManager.tvInputList
                for (info in inputList) {
                    if (info.isPassthroughInput) {
                        val label = info.loadLabel(context).toString()
                        val type = when (info.type) {
                            TvInputInfo.TYPE_HDMI -> mapHdmiType(info.id, label)
                            TvInputInfo.TYPE_COMPOSITE -> InputType.AV
                            TvInputInfo.TYPE_COMPONENT -> InputType.COMPONENT
                            TvInputInfo.TYPE_TUNER -> InputType.TUNER
                            else -> InputType.OTHER
                        }
                        list.add(
                            InputItem(
                                id = info.id,
                                label = label.ifBlank { "Input" },
                                type = type,
                                intentUri = TvContract.buildChannelUriForPassthroughInput(info.id).toString()
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(tag, "TvInputManager query failed or unsupported on this device: ${e.message}")
        }

        // If no hardware TV inputs were returned (e.g. running on generic Android / box), provide fallback standard inputs
        if (list.isEmpty()) {
            list.add(InputItem(id = "hdmi_1", label = "HDMI 1", type = InputType.HDMI_1))
            list.add(InputItem(id = "hdmi_2", label = "HDMI 2", type = InputType.HDMI_2))
            list.add(InputItem(id = "hdmi_3", label = "HDMI 3", type = InputType.HDMI_3))
            list.add(InputItem(id = "av", label = "AV / Composite", type = InputType.AV))
        }

        return list
    }

    private fun mapHdmiType(id: String, label: String): InputType {
        val combined = "$id $label".lowercase()
        return when {
            combined.contains("hdmi 1") || combined.contains("hdmi1") || combined.contains("hdmi/1") -> InputType.HDMI_1
            combined.contains("hdmi 2") || combined.contains("hdmi2") || combined.contains("hdmi/2") -> InputType.HDMI_2
            combined.contains("hdmi 3") || combined.contains("hdmi3") || combined.contains("hdmi/3") -> InputType.HDMI_3
            combined.contains("hdmi 4") || combined.contains("hdmi4") || combined.contains("hdmi/4") -> InputType.HDMI_4
            else -> InputType.HDMI_1
        }
    }

    /**
     * Launches the TV input channel intent or device-specific input switcher.
     */
    fun switchToInput(item: InputItem): Boolean {
        try {
            if (!item.intentUri.isNullOrBlank()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.intentUri)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                return true
            }

            // Fallback: Launch Live TV or TV Input chooser if available
            val fallbackIntents = listOf(
                Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("content://android.media.tv/passthrough/${item.id}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                },
                Intent("android.intent.action.TV_INPUT").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )

            for (intent in fallbackIntents) {
                if (context.packageManager.resolveActivity(intent, 0) != null) {
                    context.startActivity(intent)
                    return true
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Failed to switch to input ${item.id}", e)
        }
        return false
    }
}
