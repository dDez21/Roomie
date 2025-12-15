package com.example.roomieproject.fragment

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.roomieproject.R

class newPurchaseFragment : Fragment(R.layout.fragment_new_purchase)  {

    private lateinit var newPurchase: Button //nuova spesa

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)


        newPurchase = view.findViewById(R.id.newPurchase)


        //crea nuova spesa
        newPurchase.setOnClickListener {
            findNavController().navigate(R.id.menuFragment)
        }
    }
}