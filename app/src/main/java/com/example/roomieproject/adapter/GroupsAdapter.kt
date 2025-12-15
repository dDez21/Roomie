package com.example.roomieproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.roomieproject.R
import com.example.roomieproject.model.Group


class GroupsAdapter(
    private var groups: List<Group>,
    private var onGroupClick: (Group) -> Unit
    ) : RecyclerView.Adapter<GroupsAdapter.GroupViewHolder>() {


    //singola riga
    inner class GroupViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val groupName: TextView = itemView.findViewById(R.id.txtGroupName) //nome gruppo

        //estraggo nome gruppo da db
        fun bind(group: Group){
            groupName.text = group.groupName

            itemView.setOnClickListener{ //reazione al click
                onGroupClick(group)
            }
        }
    }

    //per creare riga
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_group, parent, false) //prendo layout singola riga
        return GroupViewHolder(view)
    }

    //per riempire riga con i dati
    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(groups[position])
    }


    //definisce numero righe
    override fun getItemCount(): Int = groups.size
}


