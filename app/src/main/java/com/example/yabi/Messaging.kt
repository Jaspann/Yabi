package com.example.yabi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.core.view.marginBottom
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_messaging.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener

class Messaging : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messaging)
        setSupportActionBar(toolbar)
        getOffers()
        toolbar.setNavigationOnClickListener {
            finish() }
    }


    private fun setRefreshRecyclers() {
        refreshOffers.setOnRefreshListener(OnRefreshListener {
            getOffers()
            refreshOffers.isRefreshing = false
        })
    }
        private fun getOffers() {
            val db = Firebase.firestore
            var queryResult: MutableList<DocumentSnapshot>
            val userID = intent.getStringExtra("userID")

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
                            if (document.get("userID") != userID) {
                                itemNames.add(document.get("itemName") as String)
                                tempLong = (document.get("your offer").toString().substringBefore('.')
                                    .toLong() as Long)
                                offerPrices.add(tempLong.toDouble())

                            }
                        } catch (e: NullPointerException) {
                            Log.e("MainActivity", "Error processing listings", e)
                        } catch (e: ClassCastException) {
                            Log.e("MainActivity", "Error casting listing types", e)
                        }
                    }

                    fillCurrentOffers(itemNames, offerPrices)
                }
                .addOnFailureListener { e ->
                    Log.w("TAG", "Failed to retrieve listings.", e)
                }
        }

        private fun fillCurrentOffers(itemNames: List<String>, offerPrices: List<Double>) {

            val data = ArrayList<OfferViewModel>()

            for (i in itemNames.indices) {
                data.add(OfferViewModel("User", itemNames[i], offerPrices[i], this))
            }

            val recyclerview = findViewById<RecyclerView>(R.id.offersRecycler)

            recyclerview.layoutManager = LinearLayoutManager(this)

            val adapter = OfferAdapter(data)

            recyclerview.adapter = adapter

        }
}