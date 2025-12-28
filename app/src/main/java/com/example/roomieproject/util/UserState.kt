package com.example.roomieproject.util

import com.example.roomieproject.model.Group

sealed class UserState {
    data object Idle : UserState()
    data object Loading : UserState()
    data class HasGroups(val groups: List<Group>) : UserState()
    data object NoGroup : UserState()
    data class Error(val e: Throwable) : UserState()
}