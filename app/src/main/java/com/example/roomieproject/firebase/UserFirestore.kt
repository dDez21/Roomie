package com.example.roomieproject.firebase

import com.example.roomieproject.model.Group
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserFirestore (
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
){
    //prendo elenco gruppi utente
    suspend fun getUserGroups(): List<Group>{
        val userUid = auth.currentUser?.uid?: throw IllegalStateException("Utente non loggato")
        val groupList = db.collection("groups").whereArrayContains("groupMembers", userUid).get().await()
        return groupList.documents.map { doc->
            val group = doc.toObject(Group::class.java)?:Group()
            group.copy(groupId = doc.id)
        }
    }

    //prendo uid utente
    suspend fun getUserData(): Pair<String, String?> {
        val userUid = auth.currentUser?.uid?: throw IllegalStateException("Utente non loggato")
        val snap = db.collection("users").document(userUid).get().await()
        val name = snap.getString("userName") ?: ""
        val logoUrl = snap.getString("userLogo")
        return name to logoUrl
    }
}