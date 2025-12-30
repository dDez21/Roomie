package com.example.roomieproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.roomieproject.R
import com.example.roomieproject.model.Group

class RequestsAdapter(
    private val onAccept: (Group) -> Unit,
    private val onDeny: (Group) -> Unit
): ListAdapter<Group, RequestsAdapter.RequestsViewHolder>(Diff){

    class RequestsViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        val groupName: TextView = itemView.findViewById(R.id.groupName)
        val acceptBtn: ImageButton = itemView.findViewById(R.id.acceptGroup)
        val denyBtn: ImageButton = itemView.findViewById(R.id.denyGroup)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_group_request, parent, false)
        return RequestsViewHolder(view)
    }

    override fun onBindViewHolder(holder: RequestsViewHolder, position: Int) {
        val group = getItem(position)
        holder.groupName.text = group.groupName
        holder.acceptBtn.setOnClickListener {onAccept(group)}
        holder.denyBtn.setOnClickListener {onDeny(group)}
    }

    companion object {
        private val Diff = object : DiffUtil.ItemCallback<Group>() {
            override fun areItemsTheSame(oldItem: Group, newItem: Group): Boolean = oldItem.groupId == newItem.groupId
            override fun areContentsTheSame(oldItem: Group, newItem: Group): Boolean = oldItem == newItem
        }
    }
}