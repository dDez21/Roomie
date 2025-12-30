package com.example.roomieproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomieproject.firebase.GroupFirestore
import com.example.roomieproject.model.Group
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class GroupSectionViewModel (
    private val group : GroupFirestore = GroupFirestore()
) : ViewModel() {

    //elenco gruppi
    private val _invitedGroups = MutableStateFlow<List<Group>>(emptyList())
    private val _searchQuery = MutableStateFlow("")

    //filtro per barra di ricerca
    val invitedGroups: StateFlow<List<Group>> =
        combine(_invitedGroups, _searchQuery) { list, q ->
            val query = q.trim().lowercase()
            if (query.isBlank()) list
            else list.filter { g ->
                g.groupName.trim().lowercase().contains(query)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    suspend fun createGroup(groupName: String): String {
        return group.createGroup(groupName)
    }

    fun refreshInvites() {
        viewModelScope.launch {
            _invitedGroups.value = group.getInvitedGroups()
        }
    }

    fun setSearchQuery(text: String) {
        _searchQuery.value = text
    }

    suspend fun acceptInvite(groupId: String): String {
        group.acceptInvite(groupId)
        return groupId
    }

    fun denyInvite(groupId:String){
        viewModelScope.launch {
            group.denyInvite(groupId)
            refreshInvites()
        }
    }
}