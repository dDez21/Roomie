package com.example.roomieproject.firebase

import android.net.Uri
import com.example.roomieproject.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class AuthFirebase(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {

    suspend fun register(
        username: String,
        email: String,
        password: String,
        imageUri: Uri?
    ): String {
        val result = auth.createUserWithEmailAndPassword(email, password).await() //creo account Auth
        val uid = result.user?.uid ?: error("uid nullo") //prendo l'id da auth
        val photo = imageUri?.let {uploadPhoto(uid, it)}  //carico immagine

        //creo utente per firestore
        val user = User(
            userName = username,
            userEmail = email,
            userLogo = photo
        )
        db.collection("users").document(uid).set(user).await() //salvo utente su firestore
        return uid
    }

    suspend fun login(
        email: String,
        password: String
    ): String{
        val result = auth.signInWithEmailAndPassword(email, password).await() //login su auth
        val uid = result.user?.uid ?: error("uid nullo") //prendo l'id da auth
        return uid
    }


    suspend fun loginWithGoogle(idToken: String): String {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        val user = result.user ?: error("User nullo dopo Google login")
        val uid = user.uid

        //creo nuovo utente se primo login
        val docRef = db.collection("users").document(uid)
        val snap = docRef.get().await()

        if (!snap.exists()) {
            val userDoc = mapOf(
                "uid" to uid,
                "username" to (user.displayName ?: "User"),
                "email" to (user.email ?: ""),
                "photo" to (user.photoUrl?.toString() ?: "")
            )
            docRef.set(userDoc).await()
        }
        return uid
    }

    private suspend fun uploadPhoto(uid: String, uri: Uri): String {
        val ref = storage.reference.child("userLogo/$uid.jpg")
        ref.putFile(uri).await()
        return ref.downloadUrl.await().toString()
    }

}