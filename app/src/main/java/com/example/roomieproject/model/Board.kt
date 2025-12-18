package com.example.roomieproject.model

import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties

@Keep
@IgnoreExtraProperties
data class Board (
    val boardNote: Note = Note(),

    //per posizionamento libero nella bacheca
    val x: Float = 0.1f,
    val y: Float = 0.1f,
    val w: Float = 0.8f,
    val h: Float = 0.2f
){
    fun isValid(): Boolean = !boardNote.isEmpty()
}