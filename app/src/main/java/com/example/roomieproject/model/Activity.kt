package com.example.roomieproject.model

data class Activity (
    val id: String = "",             //id attività
    val userId: String = "",         //id di chi ha fatto l'azione
    val userName: String = "",       //username visibile dell’utente
    val actionType: Int = 0,         //tipo azione, a numero corrisponde frase
    val timestamp: Long = 0L,        //quando svolta
    val info: String = ""            //ulteriori informazioni
)