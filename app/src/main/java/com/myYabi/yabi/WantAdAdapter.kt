package com.myYabi.yabi

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import myYabi.yabi.R


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
        if(ItemsViewModel.rating == -1)
            holder.ratingTextView.visibility = View.GONE
        else {
            val rating = ItemsViewModel.rating.toString() + "%"
            holder.ratingTextView.text = rating
        }
        holder.titleTextVew.text = ItemsViewModel.title
        holder.descTextView.text = ItemsViewModel.desc

        val mStorage = FirebaseStorage.getInstance().reference

        if(ItemsViewModel.image != "")
            mStorage.child(ItemsViewModel.image).downloadUrl.addOnSuccessListener { results ->
                Picasso.get().load(results).into(holder.imageView);
            }


        val price = "$" + String.format("%.2f", ItemsViewModel.price)
        holder.price.text = price
        holder.location.text = ItemsViewModel.location

        var shipText = "Shipping: "
        shipText += if(ItemsViewModel.shippingSeller)
            "Seller, "
        else
            "Buyer, "

        shipText += if(ItemsViewModel.shipCover == -1.0)
            "In Full"
        else
            "Up To $" + String.format("%.2f", ItemsViewModel.shipCover)
        holder.shipping.text = shipText

        holder.wantAdButton.setOnClickListener {
            if(ItemsViewModel.context.toString().substringBefore('@') == "com.myYabi.yabi.ViewOffers")
            {
                val intent = Intent(ItemsViewModel.context, PostChat::class.java)
                intent.putExtra("username", ItemsViewModel.username)
                intent.putExtra("listingID", ItemsViewModel.listingID)
                intent.putExtra("sellerID", ItemsViewModel.sellerID)
                ItemsViewModel.context.startActivity(intent)
            }
            else {
                val intent = Intent(ItemsViewModel.context, AddPost::class.java)
                intent.putExtra("isCounter", true)
                intent.putExtra("listingID", ItemsViewModel.listingID)
                intent.putExtra("sellerID", ItemsViewModel.sellerID)
                intent.putExtra("title", ItemsViewModel.title)
                intent.putExtra("price", ItemsViewModel.price)
                intent.putExtra("location", ItemsViewModel.location)
                intent.putExtra("shippingSeller", ItemsViewModel.shippingSeller)
                intent.putExtra("covering", ItemsViewModel.shipCover)
                ItemsViewModel.context.startActivity(intent)
            }
        }

        if(ItemsViewModel.tag != "" && ItemsViewModel.tag != "--")
        {
            holder.tag.text = ItemsViewModel.tag
            when(ItemsViewModel.tag)
            {
                "Furniture" -> {holder.tagCard.setCardBackgroundColor(Color.rgb(91, 128, 100))}
                "Games" -> {holder.tagCard.setCardBackgroundColor(Color.rgb(65, 49, 172))}
                "Cards" -> {holder.tagCard.setCardBackgroundColor(Color.rgb(157, 30, 30))}
                "Paintings" -> {holder.tagCard.setCardBackgroundColor(Color.rgb(103, 172, 77))}
                "Clothing" -> {holder.tagCard.setCardBackgroundColor(Color.rgb(25, 130, 168))}
                "Home Improvement" -> {holder.tagCard.setCardBackgroundColor(Color.rgb(79, 41, 9))}
                "Accessory" -> {holder.tagCard.setCardBackgroundColor(Color.rgb(146, 27, 204))}
                "Collectable" -> {holder.tagCard.setCardBackgroundColor(Color.rgb(203, 153, 27))}
            }
        }
        else
        {
            holder.tag.visibility = View.GONE
            holder.tagCard.visibility = View.GONE
        }


    }


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
        val price: TextView = itemView.findViewById(R.id.priceTextView)
        val location: TextView = itemView.findViewById(R.id.locTextView)
        val shipping: TextView = itemView.findViewById(R.id.shipTextView)
        val tag: TextView = itemView.findViewById(R.id.tagTextView)
        val tagCard: CardView = itemView.findViewById(R.id.tagCard)
    }

}