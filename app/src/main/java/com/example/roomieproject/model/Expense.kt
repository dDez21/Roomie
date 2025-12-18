package com.example.roomieproject.model

import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties


@Keep
@IgnoreExtraProperties
data class Expense (
    val expenseName: String = "",
    val expenseCost: Long = 0L,
    val expensePaidBy: String = "",
    val expenseDividedBy: List<String> = emptyList(),
    val expenseSplit: Split = Split(),
    val expenseDate: Long = 0L,
    val expenseNote: Note = Note()
) {
    fun isValid(): Boolean =
        expenseName.isNotBlank() &&
        expenseCost > 0 &&
        expensePaidBy.isNotBlank() &&
        expenseDividedBy.isNotEmpty() &&
        expenseDate > 0 &&
        expenseSplit.isChosen()
}