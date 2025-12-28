package com.example.roomieproject.firebase

import com.example.roomieproject.model.Group
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class Firestore (
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
){
    //prendo elenco gruppi utente
    suspend fun getUserGroups(uid:String): List<Group>{
        val groupList = db.collection("groups").whereArrayContains("groupMembers", uid).get().await()
        return groupList.documents.map { doc->
            val group = doc.toObject(Group::class.java)?:Group()
            group.copy(groupId = doc.id)
        }
    }

    //prendo uid utente
    suspend fun getUserData(uid: String): Pair<String, String?> {
        val snap = db.collection("users").document(uid).get().await()
        val name = snap.getString("userName") ?: ""
        val logoUrl = snap.getString("userLogo") // è l'URL (String?) oppure null
        return name to logoUrl
    }
}