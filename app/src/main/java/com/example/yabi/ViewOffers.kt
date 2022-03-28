package com.example.yabi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.activity_view_offers.*
import kotlinx.android.synthetic.main.want_ad_card.view.titleText
import kotlinx.android.synthetic.main.your_post_card.view.*

class ViewOffers : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_offers)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        fillPostCard()

        fillOffers()
    }

    private fun fillPostCard()
    {
        originalPost.offerButton.visibility = View.GONE

        val newIntent = Intent(this, AddPost::class.java)

        if(intent.hasExtra("title"))
        {
            newIntent.putExtra("title", intent.getStringExtra("title"))
            originalPost.titleText.text = intent.getStringExtra("title")
        }
        if(intent.hasExtra("desc"))
        {
            newIntent.putExtra("desc", intent.getStringExtra("desc"))
            originalPost.yourDescriptionText.text = intent.getStringExtra("desc")
        }
        if(intent.hasExtra("price"))
        {
            newIntent.putExtra("price", intent.getDoubleExtra("price", 0.00))
            originalPost.yourPriceTextView.text = String.format("$"+"%.2f",intent.getDoubleExtra("price", 0.00))
        }
        if(intent.hasExtra("location"))
        {
            newIntent.putExtra("location", intent.getStringExtra("location"))
            originalPost.yourLocTextView.text = intent.getStringExtra("location")
        }

        if(intent.hasExtra("shippingSeller")) {
            newIntent.putExtra("shippingSeller", intent.getBooleanExtra("shippingSeller", false))
        }
        if(intent.hasExtra("covering")) {
            newIntent.putExtra("covering", intent.getDoubleExtra("covering", -1.0))
        }

        var shipText = "Shipping: "
        shipText += if(intent.getBooleanExtra("shippingSeller", false))
            "Seller, "
        else
            "Buyer, "

        shipText += if(intent.getDoubleExtra("covering", -1.0) == -1.0)
            "In Full"
        else
            "Up To $" + String.format("%.2f", intent.getDoubleExtra("covering", -1.0))
        originalPost.yourShipTextView.text = shipText

        originalPost.editButton.setOnClickListener(){
            newIntent.putExtra("isEdit", true)
            this.startActivity(newIntent)
        }

    }

    private fun fillOffers()
    {
        val data = ArrayList<WantAdViewModel>()

        // Used For testing, remove when implementing database
        val users = arrayOf("Francis Lapointe", "Francis Lapointe", "Francis Lapointe")
        val scores = arrayOf(98, 33, 2)
        val titles = arrayOf("Obj I want", "Obj I want", "Obj I want")
        val desc = arrayOf("I have this object! It is in great condition and I am willing to pay shipping.",
            "I have this object! It is in great condition and I am willing to pay shipping.",
            "I have this object! It is in great condition and I am willing to pay shipping.")
        val photos = arrayOf(0, 0, 0)
        val prices = arrayOf(34.00, 33.00, 56.00)
        val locations = arrayOf("here", "there", "anywhere")
        val shippings = arrayOf(true, true, true)
        val coverings = arrayOf(14.00, 22.00, 55.98)

        var i = 0
        while (i < 30) {
            //data.add(WantAdViewModel(users[0], scores[0], titles[0], desc[0], photos[0], prices[0], locations[0], shippings[0], coverings[0], this))
            i++
        }

        val recyclerview = findViewById<RecyclerView>(R.id.offersRecycler)

        recyclerview.layoutManager = LinearLayoutManager(this)

        val adapter = WantAdAdapter(data)

        recyclerview.adapter = adapter
    }
}