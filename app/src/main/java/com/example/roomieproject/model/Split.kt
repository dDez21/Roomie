package com.example.roomieproject.model

import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties


@Keep
@IgnoreExtraProperties
data class Split (
    val type: SplitType = SplitType.UNSPECIFIED,

    val decidedSplitType: Map<String, Long> = emptyMap(),

    val finalSplitAmount: Map<String, Long> = emptyMap()
) {
    enum class SplitType { UNSPECIFIED, EQUAL, CUSTOM_AMOUNT, PERCENTAGE }
    fun isChosen(): Boolean = type != SplitType.UNSPECIFIED
}

