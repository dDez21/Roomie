package com.example.roomieproject.fragment

import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.roomieproject.R
import com.google.android.material.button.MaterialButtonToggleGroup

class GroupSectionFragment: Fragment(R.layout.fragment_group_section) {

    private lateinit var button: MaterialButtonToggleGroup
    private lateinit var newGroupLayout: LinearLayout
    private lateinit var seeRequestsLayout: LinearLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button = view.findViewById(R.id.toggleButton)
        newGroupLayout = view.findViewById(R.id.newGroupSelection)
        seeRequestsLayout = view.findViewById(R.id.seeRequestList)


        //imposto selezione di default
        newGroupLayout.visibility = View.VISIBLE
        seeRequestsLayout.visibility = View.GONE


        //imposto cambio layout allo switch
        button.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener //ignoro deselezione
            TransitionManager.beginDelayedTransition(view as ViewGroup, AutoTransition().apply {
                duration = 100
            })

            val showNew = checkedId == R.id.newGroup
            newGroupLayout.visibility = if (showNew) View.VISIBLE else View.GONE
            seeRequestsLayout.visibility = if (showNew) View.GONE else View.VISIBLE
        }
    }
}