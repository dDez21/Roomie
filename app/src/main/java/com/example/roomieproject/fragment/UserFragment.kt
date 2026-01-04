package com.example.roomieproject.fragment

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.roomieproject.R
import com.example.roomieproject.viewmodel.UserViewModel
import kotlinx.coroutines.launch


class UserFragment : Fragment(R.layout.fragment_user) {

    private val vm: UserViewModel by viewModels()
    private lateinit var userImage: ImageView
    private lateinit var imgMod: ImageButton
    private lateinit var userName: TextView
    private lateinit var usernameMod: ImageButton
    private lateinit var email: TextView
    private lateinit var emailMod: ImageButton


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userImage = view.findViewById(R.id.userImage)
        imgMod = view.findViewById(R.id.modifyImage)
        userName = view.findViewById(R.id.userName)
        usernameMod = view.findViewById(R.id.modifyName)
        emailMod = view.findViewById(R.id.modifyEmail)

        lifecycleScope.launch {
            try {
                val name = vm.getUserName()
                userName.text = if (name.isBlank()) "Utente" else name
            } catch (e: Exception) {
                userName.text = "Utente"
            }
        }

        email = view.findViewById(R.id.email)
        val userEmail = vm.getEmail()
        email.text = if (userEmail.isBlank()) "Nessuna email" else userEmail

        val localUri = vm.localAvatar(requireContext().filesDir)
        if (localUri != null) {
            Glide.with(this).load(localUri).into(userImage)
        } else {
            userImage.setImageResource(R.drawable.user_logo)
        }    }
}