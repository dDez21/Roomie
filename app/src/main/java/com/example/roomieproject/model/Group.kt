package com.example.roomieproject.model

import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties


@Keep
@IgnoreExtraProperties
data class Group(
    val groupId: String = "",
    val groupName: String = "",
    val groupLogo: String? = null,
    val groupMembers: List<String> = emptyList(),
    val groupInvited: List<String> = emptyList(),
){
    //verifico che i dati esistano
    fun isValid(): Boolean =
        groupName.isNotBlank()

    //in caso non ci fosse immagine inserita
    fun logoOrDefault(defaultUrlOrKey: String): String =
        if (!groupLogo.isNullOrBlank()) groupLogo else defaultUrlOrKey
}