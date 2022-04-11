package com.example.yabi

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import org.json.JSONException


class ChatAdapter(
    var context: Context,
    var accounts: Array<String>,
    var messages: Array<String>,
    var isOffer: Array<Double>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun getItemViewType(position: Int): Int {
        return 1
        /*
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

         */
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(context)
        val view: View
        view = inflater.inflate(R.layout.message, parent, false)
        return MessageViewHolder(view)
    }

    //@SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        (holder as MessageViewHolder).userText.text = messages[position]
        val constraintSet = ConstraintSet()
        constraintSet.clone(holder.constraint)
        if(accounts[position] == "them") {
            constraintSet.connect(
                R.id.cardBox,
                ConstraintSet.LEFT,
                R.id.messageConstraint,
                ConstraintSet.LEFT,
                5
            )
            holder.cardBox.setCardBackgroundColor(Color.DKGRAY)
        }
        if(accounts[position] == "us") {
            constraintSet.connect(
                R.id.cardBox,
                ConstraintSet.RIGHT,
                R.id.messageConstraint,
                ConstraintSet.RIGHT,
                5
            )
        }
        if(messages[position].substring(0, "listingImages/".length + 1) == "listingImages/")
        {
            holder.userImage.visibility = View.VISIBLE
            holder.userText.visibility = View.GONE

            val mStorage = FirebaseStorage.getInstance().reference

            mStorage.child(messages[position]).downloadUrl.addOnSuccessListener { results ->
                Picasso.get().load(results).into(holder.userImage)
            }
        }
        constraintSet.applyTo(holder.constraint)

        if(isOffer[position] != -1.0)
        {
            holder.offerButton.visibility = View.VISIBLE
        }

    }

    override fun getItemCount(): Int {
        return messages.size
    }

    internal class MessageViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val constraint: androidx.constraintlayout.widget.ConstraintLayout = itemView.findViewById(R.id.messageConstraint)
        val cardBox: androidx.cardview.widget.CardView = itemView.findViewById(R.id.cardBox)
        val userText: TextView = itemView.findViewById(R.id.userText)
        val userImage: ImageView = itemView.findViewById(R.id.imageView)
        val offerButton: Button = itemView.findViewById(R.id.offerButton)
    }
}