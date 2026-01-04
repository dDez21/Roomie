package com.example.roomieproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.roomieproject.R
import com.example.roomieproject.model.Group


class DrawerGroupsAdapter(
    private val onGroupClick: (groupId: String) -> Unit
) : ListAdapter<Group, DrawerGroupsAdapter.GroupViewHolder>(Diff) {

    //singola riga
    inner class GroupViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val groupName: TextView = itemView.findViewById(R.id.txtGroupName) //nome gruppo

        //estraggo nome gruppo da db
        fun bind(group: Group){
            groupName.text = group.groupName
            val selected = group.groupId == groupSelectedId
            itemView.isActivated = selected
            itemView.setOnClickListener{ //reazione al click
                onGroupClick(group.groupId)
            }
        }
    }

    //visualizzo gruppo scelto
    private var groupSelectedId: String? = null
    fun setSelected(groupId: String?){
        if (groupSelectedId == groupId) return
        val oldId = groupSelectedId
        groupSelectedId = groupId
        oldId?.let { id ->
            val oldPos = currentList.indexOfFirst { it.groupId == id }
            if (oldPos != -1) notifyItemChanged(oldPos)
        }
        groupId?.let { id ->
            val newPos = currentList.indexOfFirst { it.groupId == id }
            if (newPos != -1) notifyItemChanged(newPos)
        }
    }

    //singolo elemento
    companion object Diff : DiffUtil.ItemCallback<Group>() {
        override fun areItemsTheSame(old: Group, new: Group) =
            old.groupId == new.groupId

        override fun areContentsTheSame(old: Group, new: Group) =
            old == new
    }

    //per creare riga
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_group, parent, false) //prendo layout singola riga
        return GroupViewHolder(view)
    }

    //per riempire riga con i dati
    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}


