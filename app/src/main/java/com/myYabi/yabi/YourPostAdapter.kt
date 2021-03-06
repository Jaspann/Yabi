package com.myYabi.yabi

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import myYabi.yabi.R

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
        val mStorage = FirebaseStorage.getInstance().reference

        if(itemsViewModel.image != "")
            mStorage.child(itemsViewModel.image).downloadUrl.addOnSuccessListener { results ->
                Picasso.get().load(results).into(holder.imageView);
            }
        val offerText = "Offers"
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
            //intent.putExtra("isCounter", true)
            intent.putExtra("listingID", itemsViewModel.listingID)
            intent.putExtra("title", itemsViewModel.title)
            intent.putExtra("desc", itemsViewModel.desc)
            intent.putExtra("price", itemsViewModel.price)
            intent.putExtra("location", itemsViewModel.location)
            intent.putExtra("shippingSeller", itemsViewModel.shippingSeller)
            intent.putExtra("offers", itemsViewModel.offers)
            intent.putExtra("covering", itemsViewModel.shipCover)
            itemsViewModel.context.startActivity(intent)
        }

        holder.editButton.setOnClickListener(){
            val intent = Intent(itemsViewModel.context, AddPost::class.java)
            intent.putExtra("isEdit", true)
            intent.putExtra("title", itemsViewModel.title)
            intent.putExtra("desc", itemsViewModel.desc)
            intent.putExtra("price", itemsViewModel.price)
            intent.putExtra("location", itemsViewModel.location)
            intent.putExtra("shippingSeller", itemsViewModel.shippingSeller)
            intent.putExtra("offers", itemsViewModel.offers)
            intent.putExtra("covering", itemsViewModel.shipCover)
            itemsViewModel.context.startActivity(intent)
        }
        //TODO: holder.deleteButton.setOnClickListener()
    }


    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val titleTextVew: TextView = itemView.findViewById(R.id.titleText)
        val descTextView: TextView = itemView.findViewById(R.id.yourDescriptionText)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val price: TextView = itemView.findViewById(R.id.yourPriceTextView)
        val location: TextView = itemView.findViewById(R.id.yourLocTextView)
        val shipping: TextView = itemView.findViewById(R.id.yourShipTextView)
        val offerButton: Button = itemView.findViewById(R.id.offerButton)
        val editButton: Button = itemView.findViewById(R.id.editButton)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }
}