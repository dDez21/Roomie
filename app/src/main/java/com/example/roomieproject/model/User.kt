package com.example.roomieproject.model

import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties

@Keep
@IgnoreExtraProperties
data class User(
    val userName: String = "",
    val userEmail: String = "",
    val userLogo: String? = null
){
    //verifico che i dati esistano
    fun isValid(): Boolean =
        userName.isNotBlank() && userEmail.isNotBlank()

    //in caso non ci fosse immagine inserita
    fun logoOrDefault(defaultUrlOrKey: String): String =
        if (!userLogo.isNullOrBlank()) userLogo else defaultUrlOrKey
}
