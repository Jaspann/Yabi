package com.example.yabi

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text

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
        val price = "$" + String.format("%.2f", itemsViewModel.price)
        holder.price.text = price
        holder.location.text = itemsViewModel.location

        var shipText = "Shipping: "
        shipText += if(itemsViewModel.shippingSeller)
            "Seller, "
        else
            "Buyer, "

        shipText += if(itemsViewModel.shipCover == -1.0)
            "In Full"
        else
            "Up To $" + String.format("%.2f", itemsViewModel.shipCover)
        holder.shipping.text = shipText

        holder.offerButton.setOnClickListener {
            val intent = Intent(itemsViewModel.context, ViewOffers::class.java)
            intent.putExtra("isCounter", true)
            intent.putExtra("title", itemsViewModel.title)
            intent.putExtra("title", itemsViewModel.desc)
            intent.putExtra("price", itemsViewModel.price)
            intent.putExtra("location", itemsViewModel.location)
            intent.putExtra("shippingSeller", itemsViewModel.shippingSeller)
            intent.putExtra("offers", itemsViewModel.offers)
            intent.putExtra("covering", itemsViewModel.shipCover)
            itemsViewModel.context.startActivity(intent)
        }

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
        val price: TextView = itemView.findViewById(R.id.priceTextView)
        val location: TextView = itemView.findViewById(R.id.locTextView)
        val shipping: TextView = itemView.findViewById(R.id.shipTextView)
        val offerButton: Button = itemView.findViewById(R.id.offerButton)
        val editButton: Button = itemView.findViewById(R.id.editButton)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }
}