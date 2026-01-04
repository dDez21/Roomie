package com.example.roomieproject.fragment

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roomieproject.R
import com.example.roomieproject.adapter.MemoryAdapter
import com.example.roomieproject.viewmodel.MemoryViewModel
import kotlinx.coroutines.launch


class MemoryFragment : Fragment(R.layout.fragment_memory) {

    private val vm: MemoryViewModel by viewModels()
    private lateinit var adapter: MemoryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv = view.findViewById<RecyclerView>(R.id.memoriesContainer)

        adapter = MemoryAdapter()
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        val groupId = requireArguments().getString("groupId") ?: return
        vm.memories(groupId)

        viewLifecycleOwner.lifecycleScope.launch {
            vm.memories.collect { list ->
                adapter.submitList(list)
            }
        }
    }
}