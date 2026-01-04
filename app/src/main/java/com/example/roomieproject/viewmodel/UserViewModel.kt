package com.example.roomieproject.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.roomieproject.firebase.UserFirestore
import com.google.firebase.auth.FirebaseAuth
import java.io.File

class UserViewModel: ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = UserFirestore()


    suspend fun getUserName(): String {
        return db.getUserData()
    }

    fun getEmail(): String {
        return auth.currentUser?.email.orEmpty()
    }

    fun localAvatar(filesDir: File): Uri? {
        val uid = auth.currentUser?.uid ?: return null
        val f = File(filesDir, "avatar_$uid.jpg")
        return if (f.exists()) Uri.fromFile(f) else null
    }
}