package com.example.roomieproject.fragment

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.roomieproject.R

class CommitmentFragment : Fragment(R.layout.fragment_commitment)  {

    private lateinit var createCommit: Button //crea impegno


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        createCommit = view.findViewById(R.id.createCommit)


        //creo nuovo impegno
        createCommit.setOnClickListener {
            findNavController().navigate(R.id.menuFragment)
        }
    }
}