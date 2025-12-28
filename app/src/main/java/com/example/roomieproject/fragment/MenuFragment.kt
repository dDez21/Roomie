package com.example.roomieproject.fragment

import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.roomieproject.R
import com.google.android.material.bottomnavigation.BottomNavigationView
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
    private lateinit var newExpense: FloatingActionButton //nuova spesa
    private lateinit var bottomBar: BottomNavigationView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        //prendo l'id del gruppo corrente
        val groupId = arguments?.getString("groupId").orEmpty()
        if (groupId.isNotBlank()){
            //continuo istanziamento group
        }

        //imposto data all'inizio
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        selectedDate = calendarView.date
        currentDate = view.findViewById(R.id.currentDate)
        currentDate.text = sdf.format(Date(selectedDate))


        //aggiorno data a quella selezionata sul calendario
        calendarView = view.findViewById(R.id.calendarView)
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val cal = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            selectedDate = cal.timeInMillis
            currentDate.text = sdf.format(cal.time)
        }


        //nuovo impegno
        newCommit = view.findViewById(R.id.newCommitment)
        newCommit.setOnClickListener {
            findNavController().navigate(R.id.newCommitmentFragment)
        }


        //nuova spesa
        newExpense = view.findViewById(R.id.newExpense)
        newExpense.setOnClickListener {
            findNavController().navigate(R.id.newExpenseFragment)
        }


        //navigazione barra sotto
        bottomBar = view.findViewById(R.id.bottomBar)
        bottomBar.setupWithNavController(findNavController())
    }
}

