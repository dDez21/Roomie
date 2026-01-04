package com.example.roomieproject.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow


class GroupViewModel : ViewModel() {
    val selectedGroupId = MutableStateFlow<String?>(null)
}