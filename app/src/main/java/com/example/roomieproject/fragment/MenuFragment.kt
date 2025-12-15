package com.example.roomieproject.fragment

import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.roomieproject.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar

class MenuFragment : Fragment(R.layout.fragment_menu) {


    private lateinit var calendarView: CalendarView //calendario
    private lateinit var currentDate: TextView //data corrente
    private var selectedDate: Long = System.currentTimeMillis() //data selezionata
    private lateinit var newCommit: ImageButton //nuovo impegno
    private lateinit var newPurchase: FloatingActionButton //nuova spesa


    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        calendarView = view.findViewById(R.id.calendarView)
        currentDate = view.findViewById(R.id.currentDate)
        newCommit = view.findViewById(R.id.newCommitment)
        newPurchase = view.findViewById(R.id.newPurchase)


        //imposto data all'inizio
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        selectedDate = calendarView.date
        currentDate.text = sdf.format(Date(selectedDate))


        //aggiorno data a quella selezionata sul calendario
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val cal = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            selectedDate = cal.timeInMillis
            currentDate.text = sdf.format(cal.time)
        }


        //nuovo impegno
        newCommit.setOnClickListener {
            findNavController().navigate(R.id.newCommitmentFragment)
        }


        //nuova spesa
        newPurchase.setOnClickListener {
            findNavController().navigate(R.id.newPurchaseFragment)
        }
    }
}

