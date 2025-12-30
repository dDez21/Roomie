package com.example.roomieproject.activity

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.roomieproject.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var Email: TextInputLayout
    private lateinit var txtEmail: TextInputEditText
    private lateinit var sendEmailButton: MaterialButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() //adatta layout
        setContentView(R.layout.activity_forgot_password) //layout da usare

        auth = FirebaseAuth.getInstance()

        //collego a id del layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.forgotPassword)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Email = requireNotNull(findViewById(R.id.Email)){
            "Contenitore email (TextInputLayout) non trovato nel layout"
        }

        txtEmail = requireNotNull(findViewById(R.id.textEmail)){
            "EditText email non trovato nel layout"
        }

        sendEmailButton = requireNotNull(findViewById(R.id.sendEmail)){
            "Bottone crea account non trovato nel layout"
        }

        //al click sul bottone
        sendEmailButton.setOnClickListener {
            val email = txtEmail.text?.toString()?.trim().orEmpty()
            sendResetEmail(email)
        }
    }


    private fun sendResetEmail(email: String) {
        Email.error = null

        if (email.isEmpty()) {
            Email.error = "Inserisci la tua email"
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Email.error = "Email non valida"
            return
        }

        // disabilito il bottone per evitare doppi click
        sendEmailButton.isEnabled = false

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                sendEmailButton.isEnabled = true

                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Email di reset inviata. Controlla la posta.",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()

                } else {
                    //se ho problemi (tipo email errata ecc...)
                    Toast.makeText(
                        this,
                        "Errore durante l'invio. Riprova.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}