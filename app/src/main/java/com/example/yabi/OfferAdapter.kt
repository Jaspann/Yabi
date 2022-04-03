package com.example.yabi

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.widget.Button
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import kotlinx.android.synthetic.main.offer_card.view.*

class OfferAdapter(private val mList: List<OfferViewModel>): RecyclerView.Adapter<OfferAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.offer_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position:Int){
        val OfferViewModel = mList[position]

        holder.userTextView.text = OfferViewModel.buyer
        holder.titleTextView.text = OfferViewModel.itemName
        val offer = "$" + String.format("%.2f", OfferViewModel.youroffer)
        holder.offerTextView.text = offer

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View):RecyclerView.ViewHolder(ItemView){

            val userTextView: TextView = itemView.findViewById(R.id.userText)
            val titleTextView:TextView = itemView.findViewById(R.id.titleText)
            val offerTextView: TextView = itemView.findViewById(R.id.priceTextView)


    }
}