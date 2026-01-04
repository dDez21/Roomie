package com.example.roomieproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.roomieproject.R
import com.example.roomieproject.model.Memory
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MemoryAdapter(
    private val userNameResolver: (String) -> String = {it}
): ListAdapter<Memory, MemoryAdapter.MemoryViewHolder>(Diff)  {

    inner class MemoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val actionTitle: TextView = itemView.findViewById(R.id.actionTitle)
        private val actionDate: TextView = itemView.findViewById(R.id.actionDate)

        fun bind(memory:Memory){
            val user = userNameResolver(memory.userId)
            actionTitle.text = buildTitle(memory, user)
            actionDate.text = formatDateTime(memory.timestamp)
        }
    }

    companion object Diff : DiffUtil.ItemCallback<Memory>() {
        override fun areItemsTheSame(old: Memory, new: Memory): Boolean {
            return old.userId == new.userId &&
                    old.timestamp == new.timestamp &&
                    old.actionType == new.actionType
        }

        override fun areContentsTheSame(old: Memory, new: Memory) = old == new

        private val date = SimpleDateFormat("d/MMMM/yyyy, HH:mm", Locale("it", "IT"))

        private fun formatDateTime(ts:Long): String {
            if (ts <= 0L) return ""
            return date.format(Date(ts))
        }

        private fun formatMoney(amount: Long): String {
            val nf = NumberFormat.getCurrencyInstance(Locale.ITALY)
            return nf.format(amount)
        }

        private fun formatDateOnly(ts: Long): String {
            if (ts <= 0L) return ""
            val fmt = java.text.SimpleDateFormat("d MMMM yyyy", java.util.Locale("it", "IT"))
            return fmt.format(java.util.Date(ts))
        }

        private fun parseMillisOrNull(s: String?): Long? {
            if (s.isNullOrBlank()) return null
            return s.toLongOrNull() // se targetId contiene "1734567890000"
        }

        private fun buildTitle(m: Memory, user: String): String {
            return when (m.actionType) {
                Memory.ActionType.LEAVE_GROUP ->
                    "$user ha lasciato il gruppo"

                Memory.ActionType.EXPENSE_CREATED -> {
                    val extra = when {
                        m.amount != null && m.targetId != null ->
                            ": ${m.targetId} (${formatMoney(m.amount)})"

                        m.targetId != null ->
                            ": ${m.targetId}"

                        m.amount != null ->
                            " (${formatMoney(m.amount)})"

                        else -> ""
                    }
                    "$user ha effettuato una spesa di $extra"
                }

                Memory.ActionType.EXPENSE_DELETED ->{
                    val name = m.targetId?.takeIf { it.isNotBlank() } ?: "spesa"
                    "$user ha eliminato la spesa $name"
                }

                Memory.ActionType.PAYMENT_SENT -> {
                    val to = m.targetId?.let { " a $it" } ?: ""
                    val money = m.amount?.let { " (${formatMoney(it)})" } ?: ""
                    "$user ha pagato $to con $money"
                }

                Memory.ActionType.COMMITMENT_CREATED ->{
                    val startMillis = parseMillisOrNull(m.targetId)
                    val startText = when {
                        startMillis != null -> formatDateOnly(startMillis)
                        !m.targetId.isNullOrBlank() -> m.targetId // se ci metti già "10 ottobre 2025"
                        else -> "data non disponibile"
                    }
                    "$user ha creato un impegno per il $startText"
                }
                Memory.ActionType.COMMITMENT_DELETED ->{
                    val name = m.targetId?.takeIf { it.isNotBlank() } ?: "impegno"
                    "$user ha eliminato l'impegno $name"
                }
                else ->
                    "Azione svolta"
            }
        }

    }

    //per creare riga
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_action, parent, false) //prendo layout singola riga
        return MemoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemoryViewHolder, position: Int){
        holder.bind(getItem(position))
    }
}