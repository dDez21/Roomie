package com.example.roomieproject.fragment

import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roomieproject.R
import com.example.roomieproject.adapter.RequestsAdapter
import com.example.roomieproject.viewmodel.GroupSectionViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions

class GroupSectionFragment: Fragment(R.layout.fragment_group_section) {

    private val vm: GroupSectionViewModel by viewModels()
    private lateinit var button: MaterialButtonToggleGroup
    private lateinit var newGroupLayout: LinearLayout
    private lateinit var seeRequestsLayout: LinearLayout
    private lateinit var adapter: RequestsAdapter
    private lateinit var txtGroupName: TextInputEditText
    private lateinit var createGroup: MaterialButton
    private lateinit var searchBar: TextInputEditText


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newGroupLayout = view.findViewById(R.id.newGroupSelection)
        seeRequestsLayout = view.findViewById(R.id.seeRequestList)


        //imposto selezione di default
        newGroupLayout.visibility = View.VISIBLE
        seeRequestsLayout.visibility = View.GONE


        //imposto cambio layout allo switch
        button = view.findViewById(R.id.toggleButton)
        button.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener //ignoro deselezione
            TransitionManager.beginDelayedTransition(view as ViewGroup, AutoTransition().apply {
                duration = 100
            })
            val showNew = checkedId == R.id.newGroup
            newGroupLayout.visibility = if (showNew) View.VISIBLE else View.GONE
            seeRequestsLayout.visibility = if (showNew) View.GONE else View.VISIBLE
        }


        //creo gruppo
        createGroup = view.findViewById(R.id.CreateGroup)
        txtGroupName = view.findViewById(R.id.groupNameEditText)
        createGroup.setOnClickListener {
            val groupName = txtGroupName.text?.toString()?.trim().orEmpty()
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    createGroup.isEnabled = false
                    button.isEnabled = false
                    val groupId = vm.createGroup(groupName)
                    backMenu(groupId)
                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        e.message ?: "Errore creazione gruppo",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                createGroup.isEnabled = true
                button.isEnabled = true
            }
        }

        //riempio lista richieste
        val groupRequest = view.findViewById<RecyclerView>(R.id.groupsRecyclerView)
        adapter = RequestsAdapter(
            onAccept = { g ->
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        val groupId = vm.acceptInvite(g.groupId)
                        backMenu(groupId)
                    } catch (e: Exception) {
                        Toast.makeText(
                            requireContext(),
                            e.message ?: "Errore accettando invito",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            },
            onDeny = { g -> vm.denyInvite(g.groupId) }
        )
        groupRequest.layoutManager = LinearLayoutManager(requireContext())
        groupRequest.adapter = adapter
        vm.refreshInvites()

        //barra ricerca
        searchBar = view.findViewById<TextInputEditText>(R.id.groupSearchEditText)
        searchBar.addTextChangedListener { editable ->
            vm.setSearchQuery(editable?.toString().orEmpty())
        }

        //non ho inviti
        val noInvites = view.findViewById<TextView>(R.id.emptyInvitesText)

        val invites = view.findViewById<RecyclerView>(R.id.groupsRecyclerView)
        viewLifecycleOwner.lifecycleScope.launch {
            vm.invitedGroups.collect { list ->
                adapter.submitList(list)
                val isEmpty = list.isEmpty()
                noInvites.visibility = if (isEmpty) View.VISIBLE else View.GONE
                invites.visibility = if (isEmpty) View.GONE else View.VISIBLE
            }
        }
    }
    private fun backMenu(groupId: String){
        findNavController().navigate(
            R.id.menuFragment,
            bundleOf("groupId" to groupId),
            navOptions {
                popUpTo(R.id.menuFragment) { inclusive = true }
                launchSingleTop = true
            })
    }
}
