package com.example.roomieproject.firebase

import com.example.roomieproject.model.User
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class GoogleLinkRequiredException(
    val email: String,
    val signInMethods: List<String>,
    val pendingCredential: AuthCredential
) : Exception("Account exists for $email with methods=$signInMethods")


class AuthFirebase(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {

    suspend fun register(username: String, email: String, password: String): String {
        val result = auth.createUserWithEmailAndPassword(email, password).await() //creo account Auth
        val uid = result.user?.uid ?: error("uid nullo") //prendo l'id da auth

        //creo utente per firestore
        val user = User(
            userName = username,
            userEmail = email
        )
        db.collection("users").document(uid).set(user, SetOptions.merge()).await() //salvo utente su firestore
        return uid
    }

    suspend fun login(email: String, password: String): String{
        val result = auth.signInWithEmailAndPassword(email, password).await() //login su auth
        val uid = result.user?.uid ?: error("uid nullo") //prendo l'id da auth
        return uid
    }


    suspend fun loginWithGoogle(idToken: String): String {
        val googleCred = GoogleAuthProvider.getCredential(idToken, null)

        try {
            val result = auth.signInWithCredential(googleCred).await()
            val user = result.user ?: error("User nullo dopo Google login")
            val uid = user.uid
            val userDoc = mapOf(
                "userName" to (user.displayName ?: "User"),
                "userEmail" to (user.email ?: "")
            )
            db.collection("users").document(uid).set(userDoc, SetOptions.merge()).await()
            return uid
        } catch (e: FirebaseAuthUserCollisionException) {
            val email = e.email ?: error("Collisione ma email mancante")
            val methods = auth.fetchSignInMethodsForEmail(email).await().signInMethods ?: emptyList()
            throw GoogleLinkRequiredException(
                email = email,
                signInMethods = methods,
                pendingCredential = googleCred
            )
        }
    }

    suspend fun linkGoogleToCurrentUser(googleCredential: AuthCredential) {
        val current = auth.currentUser ?: error("Nessun utente loggato per fare link")
        current.linkWithCredential(googleCredential).await()
    }
}