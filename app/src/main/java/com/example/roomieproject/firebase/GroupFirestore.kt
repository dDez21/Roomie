package com.example.roomieproject.firebase

import com.example.roomieproject.model.Group
import com.example.roomieproject.model.Memory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class GroupFirestore (
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
){

    private fun getUserEmail(): String =
        auth.currentUser?.email?.trim()?.lowercase()?: throw IllegalStateException("Email non disponibile (utente non loggato?)")

    private fun getUserUid(): String =
        auth.currentUser?.uid ?: throw IllegalStateException("Utente non loggato")


    suspend fun createGroup(groupName: String): String{
        val userUid = getUserUid()
        val groupData = Group(
            groupName = groupName,
            groupMembers = listOf(userUid),
            groupInvited = emptyList()
        )
        val docRef = db.collection("groups").document() // genera id automatico
        docRef.set(groupData).await()
        return docRef.id
    }


    //prendo elenco inviti
    suspend fun getInvitedGroups(): List<Group>{
        val userEmail = getUserEmail()
        val invitedList = db.collection("groups").whereArrayContains("groupInvited", userEmail).get().await()
        return invitedList.documents.mapNotNull { doc ->
            doc.toObject(Group::class.java)?.copy(groupId = doc.id)
        }
    }

    //invito accettato
    suspend fun acceptInvite(groupId: String) {
        val groupRef = db.collection("groups").document(groupId)
        val userUid = getUserUid()
        val userEmail = getUserEmail()
        val batch = db.batch()
        batch.update(groupRef, "groupInvited", FieldValue.arrayRemove(userEmail))
        batch.update(groupRef, "groupMembers", FieldValue.arrayUnion(userUid))
        batch.commit().await()
    }

    //invito rifiutato
    suspend fun denyInvite(groupId: String){
        val userEmail = getUserEmail()
        val group = db.collection("groups").document(groupId)
        db.runTransaction {user ->
            user.update(group, "groupInvited", FieldValue.arrayRemove(userEmail))
            null
        }.await()
    }
}