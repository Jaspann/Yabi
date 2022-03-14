package com.example.yabi

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONException


class ChatAdapter(
    var context: Context,
    var offers: Array<Boolean>,
    var accounts: Array<String>,
    var titles: Array<String>,
    var descriptions: Array<String>,
    var prices: Array<Double>,
    var locationTo: Array<String>,
    var locationFrom: Array<String>,
    var shippingSeller: Array<Boolean>,
    var covering: Array<Double>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun getItemViewType(position: Int): Int {
        try {
            return if (offers[position]) {
                1
            } else {
                0
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(context)
        val view: View
        if (viewType == 0) {
            view = inflater.inflate(R.layout.message, parent, false)
            return MessageViewHolder(view)
        }
        else {
            view = inflater.inflate(R.layout.offer_card, parent, false)
            return OfferViewHolder(view)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if(offers[position])
        {

            (holder as OfferViewHolder).titleTextVew.text = titles[position]
            if(accounts[position] == "us")
                holder.userTextVew.text = "Your Offer"
            else
                holder.userTextVew.text = accounts[position] + "'s Offer"

            holder.descTextView.text = descriptions[position]
            holder.priceTextView.text = String.format("$%.2f", prices[position])
            val fromText = "From: " + locationFrom[position]
            holder.locationFromTextView.text = fromText
            val toText = "To: " + locationTo[position]
            holder.locationToTextView.text = toText

            var shipText = "Shipping: "
            shipText += if(shippingSeller[position])
                "Seller, "
            else
                "Buyer, "

            shipText += if(covering[position] == -1.0)
                "In Full"
            else
                "Up To $" + String.format("%.2f", covering[position])
            holder.shippingTextView.text = shipText
        }
        else
        {
            (holder as MessageViewHolder).userText.text = descriptions[position]
            val constraintSet = ConstraintSet()
            constraintSet.clone(holder.constraint)
            if(accounts[position] == "them")
                constraintSet.connect(R.id.cardBox,ConstraintSet.LEFT,R.id.messageConstraint,ConstraintSet.LEFT,5)
            if(accounts[position] == "us")
                constraintSet.connect(R.id.cardBox,ConstraintSet.RIGHT,R.id.messageConstraint,ConstraintSet.RIGHT,5)
            constraintSet.applyTo(holder.constraint);

        }
    }

    override fun getItemCount(): Int {
        return offers.size
    }

    internal class OfferViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val userTextVew: TextView = itemView.findViewById(R.id.offerUserText)
        val titleTextVew: TextView = itemView.findViewById(R.id.titleText)
        val descTextView: TextView = itemView.findViewById(R.id.descriptionText)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val priceTextView: TextView = itemView.findViewById(R.id.priceTextView)
        val locationFromTextView: TextView = itemView.findViewById(R.id.locTextView)
        val locationToTextView: TextView = itemView.findViewById(R.id.fromTextView)
        val shippingTextView: TextView = itemView.findViewById(R.id.shipTextView)

    }

    internal class MessageViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val constraint: androidx.constraintlayout.widget.ConstraintLayout = itemView.findViewById(R.id.messageConstraint)
        val cardBox: androidx.cardview.widget.CardView = itemView.findViewById(R.id.cardBox)
        val userText: TextView = itemView.findViewById(R.id.userText)
    }
}