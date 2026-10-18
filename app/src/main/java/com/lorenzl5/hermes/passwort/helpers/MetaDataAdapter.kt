package com.lorenzl5.hermes.passwort.helpers

import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.lorenzl5.hermes.passwort.R
import com.lorenzl5.hermes.passwort.database.MetaData

class MetaDataAdapter(
    private var items: List<MetaData>,
    private val onClick: (MetaData) -> Unit,
    private val onLongClick: (MetaData) -> Unit,
    private val onSwipeDelete: (MetaData) -> Unit
) : RecyclerView.Adapter<MetaDataAdapter.ViewHolder>() {

    inner class ViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
        val domain: android.widget.TextView
        val username: android.widget.TextView
        val charset: android.widget.TextView
        val length: android.widget.TextView
        val version: android.widget.TextView

        init {
            domain = view.findViewById(R.id.card_domain)
            username = view.findViewById(R.id.card_username)
            charset = view.findViewById(R.id.card_charset)
            length = view.findViewById(R.id.card_length)
            version = view.findViewById(R.id.card_version)
        }
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context
        holder.domain.text = item.domain
        holder.username.text = context.getString(R.string.username_label) + " " + item.username
        holder.charset.text = context.getString(R.string.character_set_label) + " " + item.characterSetDescription()
        holder.length.text = context.getString(R.string.length_label) + " " + item.length
        holder.version.text = context.getString(R.string.version_label) + " " + item.iteration

        holder.itemView.setOnClickListener { onClick(item) }
        holder.itemView.setOnLongClickListener {
            onLongClick(item)
            true
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<MetaData>) {
        items = newItems
        notifyDataSetChanged()
    }
}
