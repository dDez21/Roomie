package com.example.roomieproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomieproject.firebase.UserFirestore
import com.example.roomieproject.util.UserState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MenuViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = UserFirestore()
    //verifica se utente ha già gruppi o no
    private val _userState = MutableStateFlow<UserState>(UserState.Idle)
    val userState: StateFlow<UserState> = _userState
    private var alreadyCheck = false //evito che l'operazione venga ripetuta

    fun checkUser(){
        if(alreadyCheck) return
        alreadyCheck = true

        val userUid = auth.currentUser?.uid
        if (userUid == null){
            _userState.value = UserState.Error(IllegalStateException("Utente non loggato"))
            return
        }

        viewModelScope.launch {
            _userState.value = UserState.Loading
            try{
                val groupList = db.getUserGroups()
                _userState.value =
                    if(groupList.isEmpty()) UserState.NoGroup
                    else UserState.HasGroups(groupList)
            }
            catch (e: Exception) {
                _userState.value = UserState.Error(e)
            }
        }
    }

    suspend fun userData(): Pair<String, String?> {
        return db.getUserData()
    }

    fun refreshGroups() {
        alreadyCheck = false
        checkUser()
    }
}