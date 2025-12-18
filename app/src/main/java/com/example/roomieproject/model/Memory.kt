package com.example.roomieproject.model

import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties


@Keep
@IgnoreExtraProperties
data class Memory (
    val userId: String = "",  //id di chi ha fatto l'azione
    val actionType: ActionType = ActionType.UNSPECIFIED,  //tipo azione, a numero corrisponde frase
    val timestamp: Long = 0L,  //quando
    val targetId: String? = null,  //eventuale utente che riceve
    val amount: Long? = null  //eventuale cifra
){
    fun isValid(): Boolean =
        userId.isNotBlank() &&
        actionType != ActionType.UNSPECIFIED &&
        timestamp > 0

    enum class ActionType {
            UNSPECIFIED,
            JOIN_GROUP,
            LEAVE_GROUP,
            EXPENSE_CREATED,
            PAYMENT_SENT,
            COMMITMENT_CREATED,
            COMMITMENT_DELETED
        }
}
