package com.example.roomieproject.activity

import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.roomieproject.R
import com.example.roomieproject.viewmodel.AuthViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.roomieproject.util.AuthState
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream


class RegisterActivity : AppCompatActivity() {

    private val vm: AuthViewModel by viewModels()
    private lateinit var arrowBack: ImageButton
    private lateinit var Username: TextInputLayout
    private lateinit var txtUsername: TextInputEditText
    private lateinit var addProfilePic: FloatingActionButton
    private var userPicUrl: Uri? = null
    private lateinit var Email: TextInputLayout
    private lateinit var txtEmail: TextInputEditText
    private lateinit var Password: TextInputLayout
    private lateinit var txtPassword: TextInputEditText
    private lateinit var registerButton: MaterialButton


    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val localUri = try {copyImageToInternalStorage(uri)}
            catch (e: Exception) {
                Log.e("REGISTER", "Copia immagine fallita", e)
                Toast.makeText(this, "Errore salvataggio foto", Toast.LENGTH_SHORT).show()
                return@registerForActivityResult
            }

            userPicUrl = localUri
            addProfilePic.setImageURI(localUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        //collego a id del layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //associo riferimenti variabili istanziate
        Username = findViewById(R.id.username)
        txtUsername = findViewById(R.id.txtUsername)
        Email = findViewById(R.id.Email)
        txtEmail = findViewById(R.id.textEmail)
        Password = findViewById(R.id.Password)
        txtPassword = findViewById(R.id.textPassword)
        registerButton = findViewById(R.id.CreateAccount)


        //imposto azioni
        arrowBack = findViewById(R.id.arrowBack)
        arrowBack.setOnClickListener {
        onBackPressedDispatcher.onBackPressed()
        }

        addProfilePic = findViewById(R.id.addIcon)
        addProfilePic.setOnClickListener {
            openGallery()
        }

        //gestione navigazione
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.state.collect { state->
                    when (state) {
                        //evito sommarsi di chiamate
                        AuthState.Idle -> {
                            registerButton.isEnabled = true
                            arrowBack.isEnabled = true
                        }
                        AuthState.Loading -> {
                            registerButton.isEnabled = false
                            arrowBack.isEnabled = false
                        }

                        //utente creato
                        is AuthState.Success -> {
                            persistAvatarForUid(state.uid)
                            val intent = Intent(this@RegisterActivity, MenuActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            startActivity(intent)
                            vm.setIdle()
                        }

                        //errore
                        is AuthState.Error -> {
                            Log.e("REGISTER", "Register failed", state.e)
                            Toast.makeText(this@RegisterActivity, state.e.localizedMessage ?: "Errore registrazione", Toast.LENGTH_LONG).show()
                            vm.setIdle()

                        }
                    }
                }
            }
        }


        registerButton.setOnClickListener {
            val username = txtUsername.text?.toString()?.trim().orEmpty()
            val email = txtEmail.text?.toString()?.trim().orEmpty()
            val password = txtPassword.text?.toString()?.trim().orEmpty()

            if (!validateInput(username, email, password)) return@setOnClickListener
            vm.register(username, email, password)
        }
    }

    private fun copyImageToInternalStorage(sourceUri: Uri): Uri {
        val destFile = File(filesDir, "avatar_tmp_${System.currentTimeMillis()}.jpg")
        contentResolver.openInputStream(sourceUri).use { input ->
            requireNotNull(input) { "InputStream nullo" }
            FileOutputStream(destFile).use { output -> input.copyTo(output) }
        }
        return destFile.toUri()
    }

    private fun persistAvatarForUid(uid: String) {
        val current = userPicUrl ?: return
        val srcPath = current.path ?: return
        val src = File(srcPath)
        if (!src.exists()) return
        val dest = File(filesDir, "avatar_$uid.jpg")
        src.copyTo(dest, overwrite = true)
        src.delete()
        userPicUrl = dest.toUri()
    }

    private fun hasNetwork(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val net = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(net) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }


    //verifico validità dati
    private fun validateInput(username: String, email: String, password: String): Boolean {
        var isValid = true

        // pulisco errori precedenti
        Username.error = null
        Email.error = null
        Password.error = null

        if (username.isBlank()) {
            Username.error = "Inserisci uno username"
            isValid = false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Email.error = "Inserisci una email valida"
            isValid = false
        }
        if (password.length < 8) {
            Password.error = "La password deve avere almeno 8 caratteri"
            isValid = false
        }
        if (!hasNetwork()) {
            Toast.makeText(this, "Network connection error", Toast.LENGTH_SHORT).show()
            isValid = false
        }
        return isValid
    }

    private fun openGallery() {
        pickImage.launch("image/*")
    }
}