package com.example.roomieproject.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.roomieproject.R
import com.example.roomieproject.viewmodel.AuthViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.roomieproject.util.AuthState
import kotlinx.coroutines.launch


class RegisterActivity : AppCompatActivity() {

    private val vm: AuthViewModel by viewModels()
    private lateinit var arrowBack: ImageButton
    private lateinit var Username: TextInputLayout
    private lateinit var txtUsername: TextInputEditText
    private lateinit var addProfilePic: FloatingActionButton
    private var userPicUrl: android.net.Uri? = null
    private lateinit var Email: TextInputLayout
    private lateinit var txtEmail: TextInputEditText
    private lateinit var Password: TextInputLayout
    private lateinit var txtPassword: TextInputEditText
    private lateinit var registerButton: MaterialButton



    // Per aprire la galleria
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
        uri ->if (uri != null) {
            userPicUrl = uri
            addProfilePic.setImageURI(uri)  //Sostituisce l’icona del FAB con la foto scelta
        }
    }

    // Per chiedere i permessi
    private val permissionRequest = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        granted ->
            if (granted) openGallery()
            else Toast.makeText(this, "Permesso negato", Toast.LENGTH_SHORT).show()
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
        arrowBack = findViewById(R.id.arrowBack)
        Username = findViewById(R.id.username)
        txtUsername = findViewById(R.id.txtUsername)
        addProfilePic = findViewById(R.id.addIcon)
        Email = findViewById(R.id.Email)
        txtEmail = findViewById(R.id.textEmail)
        Password = findViewById(R.id.Password)
        txtPassword = findViewById(R.id.textPassword)
        registerButton = findViewById(R.id.CreateAccount)


        //imposto azioni
        arrowBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        addProfilePic.setOnClickListener {
            selectImage()
        }

        //gestione navigazione
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.state.collect { state->
                    when (state) {
                        //evito sommarsi di chiamate
                        AuthState.Idle -> registerButton.isEnabled = true
                        AuthState.Loading -> registerButton.isEnabled = false

                        //utente creato
                        is AuthState.Success -> {
                            val intent = Intent(this@RegisterActivity, MenuActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            startActivity(intent)
                        }

                        //errore
                        is AuthState.Error -> {
                            Toast.makeText(
                                this@RegisterActivity,
                                state.e.message ?: "Errore registrazione",
                                Toast.LENGTH_SHORT
                            ).show()
                            vm.setIdle()
                        }
                }
            }
        }}


        registerButton.setOnClickListener {
            val username = txtUsername.text?.toString()?.trim().orEmpty()
            val email = txtEmail.text?.toString()?.trim().orEmpty()
            val password = txtPassword.text?.toString()?.trim().orEmpty()

            if (!validateInput(username, email, password)) return@setOnClickListener
            vm.register(username, email, password, userPicUrl)
        }
    }

    private fun hasNetwork(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val nc = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
        return nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
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



    private fun selectImage() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, permission)
            == PackageManager.PERMISSION_GRANTED
        ) {
            openGallery()
        } else {
            permissionRequest.launch(permission)
        }
    }

    private fun openGallery() {
        pickImage.launch("image/*")
    }
}