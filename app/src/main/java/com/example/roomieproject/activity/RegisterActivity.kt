package com.example.roomieproject.activity

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.CredentialManager
import com.example.roomieproject.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

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
            "Aggiungi foto profilo non trovata nel layout"
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




    }
}