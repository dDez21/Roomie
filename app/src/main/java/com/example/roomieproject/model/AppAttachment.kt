package com.example.roomieproject.model

import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties


@Keep
@IgnoreExtraProperties
data class AppAttachment (
    val type: AttachmentType = AttachmentType.IMAGE,
    val url: String = ""
) {
    enum class AttachmentType { IMAGE, AUDIO }
}