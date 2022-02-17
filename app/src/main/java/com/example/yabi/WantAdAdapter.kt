package com.example.yabi

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

class WantAdAdapter(private val mList: List<WantAdViewModel>) : RecyclerView.Adapter<WantAdAdapter.ViewHolder>() {

    //var context : Context

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.want_ad_card, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]

        // sets the text to the textview from our itemHolder class
        holder.userTextView.text = ItemsViewModel.username
        val rating = ItemsViewModel.rating.toString() + "%"
        holder.ratingTextView.text = rating
        holder.titleTextVew.text = ItemsViewModel.title
        holder.descTextView.text = ItemsViewModel.desc
        holder.imageView.setImageResource(ItemsViewModel.img)

        holder.wantAdButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val intent = Intent(ItemsViewModel.context, Account::class.java)
                ItemsViewModel.context.startActivity(intent)
            }})


    }
/*
    fun onPressWantAd()
    {
        val intent = Intent(context, Account::class.java)
        context.startActivity(intent)
    }*/


    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val userTextView: TextView = itemView.findViewById(R.id.userText)
        val ratingTextView: TextView = itemView.findViewById(R.id.ratingText)
        val titleTextVew: TextView = itemView.findViewById(R.id.titleText)
        val descTextView: TextView = itemView.findViewById(R.id.descriptionText)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val wantAdButton: Button = itemView.findViewById(R.id.wantAdButton)
    }

}