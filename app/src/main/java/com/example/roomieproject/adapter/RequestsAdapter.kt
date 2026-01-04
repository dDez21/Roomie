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

    inner class RequestsViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        private val groupName: TextView = itemView.findViewById(R.id.groupName)
        private val acceptBtn: ImageButton = itemView.findViewById(R.id.acceptGroup)
        private val denyBtn: ImageButton = itemView.findViewById(R.id.denyGroup)

        //estraggo inviti
        fun bind(group: Group){
            groupName.text = group.groupName
            acceptBtn.setOnClickListener {onAccept(group)}
            denyBtn.setOnClickListener {onDeny(group)}
        }
    }

    //singolo elemento
    companion object Diff: DiffUtil.ItemCallback<Group>() {
        override fun areItemsTheSame(oldItem: Group, newItem: Group): Boolean = oldItem.groupId == newItem.groupId
        override fun areContentsTheSame(oldItem: Group, newItem: Group): Boolean = oldItem == newItem
    }

    //per creare riga
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_group_request, parent, false) //layout singola riga
        return RequestsViewHolder(view)
    }

    //per riempire riga con i dati
    override fun onBindViewHolder(holder: RequestsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}