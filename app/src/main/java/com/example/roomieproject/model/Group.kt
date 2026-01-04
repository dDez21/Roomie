package com.example.roomieproject.model

import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties


@Keep
@IgnoreExtraProperties
data class Group(
    val groupId: String = "",
    val groupName: String = "",
    val groupMembers: List<String> = emptyList(),
    val groupInvited: List<String> = emptyList()
)