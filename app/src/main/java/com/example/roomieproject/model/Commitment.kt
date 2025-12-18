package com.example.roomieproject.model

import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties


@Keep
@IgnoreExtraProperties
data class Commitment (
    val commitName: String = "",
    var commitCategory: String?,
    var commitTimeStart: Long = 0L,
    val commitTimeEnd: Long? = null,
    val commitNote: Note = Note()
){
    fun isValid(): Boolean =
    commitName.isNotBlank() &&
    commitTimeStart > 0 &&
    (commitTimeEnd == null || commitTimeEnd >= commitTimeStart)
}