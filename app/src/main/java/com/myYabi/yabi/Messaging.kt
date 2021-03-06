package com.myYabi.yabi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_messaging.*
//import kotlinx.android.synthetic.main.activity_main.*
import myYabi.yabi.R

class Messaging : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messaging)
        setSupportActionBar(toolbar)
        val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        setRefresh()
        getOffers()
        toolbar.setNavigationOnClickListener {
            finish()
        }

    }
    private fun setRefresh(){
        refreshChatList.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            refreshChatList.isRefreshing = false
            getOffers()
        })
    }

    private fun fillOffers(listingIDs: List<String>, emails : List<String>, itemNames: List<String>, offerPrices: List<Double>)
    {
        val data = ArrayList<OfferViewModel>()
        for (i in itemNames.indices) {
            data.add(OfferViewModel(listingIDs[i], emails[i], itemNames[i], offerPrices[i], this))
        }

        val recyclerview = findViewById<RecyclerView>(R.id.ChatListRecycler)

        recyclerview.layoutManager = LinearLayoutManager(this)

        val adapter = OfferAdapter(data)

        recyclerview.adapter = adapter

    }

    private fun getOffers() {
        val db = Firebase.firestore
        var queryResult: MutableList<DocumentSnapshot>
        val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        val userID = sharedPreferences.getString("userID", "guest")
        db.collection("offers")
            .orderBy("itemName", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { results ->
                Log.d("TAG", "offers retrieved.")
                queryResult = results.documents
                var tempString = ""
                val itemNames = arrayListOf<String>()
                val offerPrices = arrayListOf<Double>()
                val emails = arrayListOf<String>()
                val listingIDs = arrayListOf<String>()
                for (document in queryResult) {
                    try {
                        if (document.get("buyerID") == userID) {
                            emails.add(document.get("buyerEmail").toString())
                            itemNames.add(document.get("itemName").toString())
                            tempString = (document.get("yourOffer").toString())
                            offerPrices.add(tempString.toDouble())
                            listingIDs.add(document.get("listingID").toString())
                        }
                    } catch (e: NullPointerException) {
                        Log.e("MainActivity", "Error processing listings", e)
                    } catch (e: ClassCastException) {
                        Log.e("MainActivity", "Error casting listing types", e)
                    }
                }

                fillOffers(listingIDs, emails ,itemNames, offerPrices)
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Failed to retrieve listings.", e)
            }
    }
}