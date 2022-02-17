package com.example.yabi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.RecyclerView

class YourPostAdapter(private val mList: List<YourPostViewModel>) : RecyclerView.Adapter<YourPostAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.your_post_card, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemsViewModel = mList[position]

        holder.titleTextVew.text = itemsViewModel.title
        holder.descTextView.text = itemsViewModel.desc
        holder.imageView.setImageResource(itemsViewModel.img)
        val offerText = itemsViewModel.offers.toString() + " Offers"
        holder.offerButton.text = offerText

        //TODO: holder.offerButton.setOnClickListener()
        //TODO: holder.editButton.setOnClickListener()
        //TODO: holder.deleteButton.setOnClickListener()

    }


    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val titleTextVew: TextView = itemView.findViewById(R.id.titleText)
        val descTextView: TextView = itemView.findViewById(R.id.descriptionText)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val offerButton: Button = itemView.findViewById(R.id.offerButton)
        val editButton: Button = itemView.findViewById(R.id.editButton)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }
}