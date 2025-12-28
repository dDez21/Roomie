package com.example.roomieproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomieproject.firebase.Firestore
import com.example.roomieproject.model.User
import com.example.roomieproject.util.UserState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MenuViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = Firestore()
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
                val groupList = db.getUserGroups(userUid)
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
        val uid = auth.currentUser?.uid ?: return "" to null
        return db.getUserData(uid)
    }
}