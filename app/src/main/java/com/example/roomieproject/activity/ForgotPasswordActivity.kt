package com.example.roomieproject.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.roomieproject.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var Email: TextInputLayout
    private lateinit var txtEmail: TextInputEditText
    private lateinit var sendEmailButton: MaterialButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() //adatta layout
        setContentView(R.layout.activity_forgot_password) //layout da usare

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
    }
}