package com.example.roomieproject.firebase

import com.example.roomieproject.model.Memory
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import com.google.firebase.firestore.Query


class MemoryFirestore(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
){
    fun memories(groupId:String, days: Int=30, limit: Long=100): Flow<List<Memory>> = callbackFlow{
        val cutoffMs = System.currentTimeMillis() - days.toLong() * 24 * 60 * 60 * 1000

        val el = db.collection("groups").document(groupId).collection("memories").whereGreaterThanOrEqualTo("timestamp", cutoffMs).orderBy("timestamp", Query.Direction.DESCENDING).limit(limit)

        var reg: ListenerRegistration? = null
        reg = el.addSnapshotListener { snap, err ->
            if (err != null) {
                close(err)
                return@addSnapshotListener
            }
            val list = snap?.documents?.mapNotNull { it.toObject(Memory::class.java) }.orEmpty()
            trySend(list).isSuccess
        }
        awaitClose{reg?.remove()}
    }
}