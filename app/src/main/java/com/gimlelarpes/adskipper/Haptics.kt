package com.gimlelarpes.adskipper

import androidx.compose.ui.hapticfeedback.HapticFeedbackType

fun hapticTypeSwitch(state: Boolean): HapticFeedbackType {
    return  if (state) HapticFeedbackType.LongPress else HapticFeedbackType.TextHandleMove
}
fun hapticTypeSlider(): HapticFeedbackType {
    return   HapticFeedbackType.TextHandleMove
}