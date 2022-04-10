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
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener

class ViewOffers : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_offers)
        setSupportActionBar(toolbar)
        val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        setRefresh()

        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setRefresh(){
        refreshOffers.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            refreshOffers.isRefreshing = false
            getOffers()
        })
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        fillPostCard()
        getOffers()
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

    private fun fillOffers(itemNames: List<String>, offerPrices: List<Double>)
    {
        val data = ArrayList<OfferViewModel>()
        for (i in itemNames.indices) {
            data.add(OfferViewModel("user", itemNames[i], offerPrices[i], this))
        }

        val recyclerview = findViewById<RecyclerView>(R.id.offersRecycler)

        recyclerview.layoutManager = LinearLayoutManager(this)

        val adapter = OfferAdapter(data)

        recyclerview.adapter = adapter

    }

    private fun getOffers() {
        val db = Firebase.firestore
        var queryResult: MutableList<DocumentSnapshot>
        val userID = intent.getStringExtra("userID")
        val itemName = originalPost.titleText.text
        db.collection("offer")
            .orderBy("itemName", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { results ->
                Log.d("TAG", "offers retrieved.")
                queryResult = results.documents

                var tempLong: Long
                var tempLongTwo: Long
                val itemNames = arrayListOf<String>()
                val offerPrices = arrayListOf<Double>()
                for (document in queryResult) {
                    try {
                        if (document.get("userID") == userID && document.get("itemName") == itemName) {
                            itemNames.add(document.get("itemName") as String)
                            tempLong = (document.get("youroffer").toString().substringBefore('.')
                                .toLong() as Long)
                            offerPrices.add(tempLong.toDouble())

                        }
                    } catch (e: NullPointerException) {
                        Log.e("MainActivity", "Error processing listings", e)
                    } catch (e: ClassCastException) {
                        Log.e("MainActivity", "Error casting listing types", e)
                    }
                }

                fillOffers(itemNames, offerPrices)
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Failed to retrieve listings.", e)
            }
    }

}