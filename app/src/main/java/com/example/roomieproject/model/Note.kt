package com.example.roomieproject.model

import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties

@Keep
@IgnoreExtraProperties
data class Note (
    val text: String? = null,
    val attachments: List<AppAttachment> = emptyList()
) {
    fun isEmpty(): Boolean = text.isNullOrBlank() && attachments.isEmpty()
}