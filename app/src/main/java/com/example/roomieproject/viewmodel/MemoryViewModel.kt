package com.example.roomieproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomieproject.firebase.MemoryFirestore
import com.example.roomieproject.model.Memory
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MemoryViewModel (
    private val repo: MemoryFirestore = MemoryFirestore()
) : ViewModel() {

    private val _memories = MutableStateFlow<List<Memory>>(emptyList())
    val memories: StateFlow<List<Memory>> = _memories
    private var listenJob: Job? = null

    fun memories(groupId: String, days: Int = 30, limit: Long = 100) {
        listenJob?.cancel()

        listenJob = viewModelScope.launch {
            repo.memories(groupId, days, limit).collect { list ->
                _memories.value = list.filter {it.isValid()}
            }
        }
    }
}

