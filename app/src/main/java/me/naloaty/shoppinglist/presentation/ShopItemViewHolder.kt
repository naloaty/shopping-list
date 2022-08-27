package me.naloaty.shoppinglist.presentation

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.naloaty.shoppinglist.R

class ShopItemViewHolder(val view: View): RecyclerView.ViewHolder(view) {
    val tvTitle = view.findViewById<TextView>(R.id.tv_title)
    val tvCount = view.findViewById<TextView>(R.id.tv_count)
}