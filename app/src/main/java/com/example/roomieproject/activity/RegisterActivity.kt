package com.example.roomieproject.activity

import android.Manifest
import android.content.pm.PackageManager
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
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.UserProfileChangeRequest

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var arrowBack: ImageButton
    private lateinit var Username: TextInputLayout
    private lateinit var txtUsername: TextInputEditText
    private lateinit var addProfilePic: FloatingActionButton
    private lateinit var Email: TextInputLayout
    private lateinit var txtEmail: TextInputEditText
    private lateinit var Password: TextInputLayout
    private lateinit var txtPassword: TextInputEditText
    private lateinit var registerButton: MaterialButton


    // Per aprire la galleria
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
        uri ->if (uri != null) {
                    addProfilePic.setImageURI(uri)}  //Sostituisce l’icona del FAB con la foto scelta
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

        auth = FirebaseAuth.getInstance() //per autenticazione

        //collego a id del layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        arrowBack = requireNotNull(findViewById(R.id.arrowBack)){
            "Freccia indietro non trovata nel layout"
        }

        Username = requireNotNull(findViewById(R.id.username)){
            "Contenitore username (TextInputLayout) non trovato nel layout"
        }

        txtUsername = requireNotNull(findViewById(R.id.txtUsername)){
            "EditText username non trovato nel layout"
        }

        addProfilePic = requireNotNull(findViewById(R.id.addIcon)){
            "Bottone Aggiungi foto profilo non trovata nel layout"
        }

        Email = requireNotNull(findViewById(R.id.Email)){
            "Contenitore email (TextInputLayout) non trovato nel layout"
        }

        txtEmail = requireNotNull(findViewById(R.id.textEmail)){
            "EditText email non trovato nel layout"
        }

        Password = requireNotNull(findViewById(R.id.Password)){
            "Contenitore password (TextInputLayout) non trovato nel layout"
        }

        txtPassword = requireNotNull(findViewById(R.id.textPassword)){
            "EditText password non trovato nel layout"
        }

        registerButton = requireNotNull(findViewById(R.id.CreateAccount)){
            "Bottone crea account non trovato nel layout"
        }



        //imposto azioni
        arrowBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        addProfilePic.setOnClickListener {
            selectImage()
        }


        registerButton.setOnClickListener {
            val username = txtUsername.text?.toString()?.trim().orEmpty()
            val email = txtEmail.text?.toString()?.trim().orEmpty()
            val password = txtPassword.text?.toString()?.trim().orEmpty()

            if (!validateInput(username, email, password)) return@setOnClickListener

            createAccount(username, email, password)
        }
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
        return isValid
    }


    //creo account
    private fun createAccount(username: String, email: String, password: String) {
        registerButton.isEnabled = false

        //vengono effettuate verifiche da Firebase su email e ppw
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                registerButton.isEnabled = true

                //risultato operazione
                if (task.isSuccessful) {
                    val user = task.result?.user

                    // aggiorno il displayName con lo username
                    user?.let {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(username)
                            .build()
                        it.updateProfile(profileUpdates)
                    }

                    Toast.makeText(
                        this,
                        "Account creato! Ora puoi effettuare il login.",
                        Toast.LENGTH_SHORT
                    ).show()

                    // torno alla schermata precedente (LoginActivity)
                    finish()

                } else {
                    val e = task.exception
                    val code = (e as? FirebaseAuthException)?.errorCode

                    when (code) {
                        "ERROR_EMAIL_ALREADY_IN_USE" ->
                            Email.error = "Email già registrata"
                        "ERROR_INVALID_EMAIL" ->
                            Email.error = "Email non valida"
                        "ERROR_WEAK_PASSWORD" ->
                            Password.error = "Password troppo debole"
                        "ERROR_NETWORK_REQUEST_FAILED" ->
                            Toast.makeText(this, "Errore di rete", Toast.LENGTH_SHORT).show()
                        else ->
                            Toast.makeText(
                                this,
                                "Registrazione fallita${if (code != null) " ($code)" else ""}",
                                Toast.LENGTH_SHORT
                            ).show()
                    }
                }
            }
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