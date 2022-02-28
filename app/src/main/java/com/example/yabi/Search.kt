package com.example.yabi

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import android.content.DialogInterface
import android.util.Log
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.activity_search.*

class Search : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                getData()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        refreshSearch.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            refreshSearch.isRefreshing = false
            getData()
        })
    }

    private fun getData()
    {
        val db = Firebase.firestore
        var queryResult: MutableList<DocumentSnapshot>

        db.collection("listings")
            .orderBy("creationTimestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { results ->
                Log.d("TAG", "Listings retrieved.")
                queryResult = results.documents
                //TODO: Find permanent solution to workaround in assignment of longs
                var tempLong: Long
                var tempLongTwo: Long
                val itemNames = arrayListOf<String>()
                val itemDescriptions = arrayListOf<String>()
                val itemPrices = arrayListOf<Double>()
                val locations = arrayListOf<String>()
                val coverShipping = arrayListOf<Boolean>()
                val coveredShipping = arrayListOf<Double>()
                for (document in queryResult) {
                    try {
                        itemNames.add(document.get("itemName") as String)
                        itemDescriptions.add(document.get("itemDescription") as String)
                        tempLong = document.get("requestedPrice").toString().substringBefore('.').toLong() as Long
                        itemPrices.add(tempLong.toDouble())
                        locations.add(document.get("shippingCity") as String + ", " + document.get("shippingState") as String)
                        coverShipping.add(document.get("coverShipping") as Boolean)
                        tempLongTwo = document.get("coveredShipping").toString().substringBefore('.').toLong() as Long
                        coveredShipping.add(tempLongTwo.toDouble())
                    } catch(e: NullPointerException) {
                        Log.e("MainActivity", "Error processing listings", e)
                    } catch(e: ClassCastException) {
                        Log.e("MainActivity", "Error casting listing types", e)
                    }
                }

                fillSearch(itemNames, itemDescriptions, itemPrices, locations, coverShipping, coveredShipping)
            }
            .addOnFailureListener{ e ->
                Log.w("TAG", "Failed to retrieve listings.", e)
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_nav_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.filter_search_button -> {
                showFilterAlert()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun fillSearch(itemNames: List<String>, itemDescriptions: List<String>, itemPrices: List<Double>,
                 locations: List<String>, coverShipping: List<Boolean>, coveredShipping: List<Double>)
    {
        val data = ArrayList<WantAdViewModel>()

        //val photos = arrayOf(0)

        for (i in itemNames.indices) {
            data.add(WantAdViewModel("User", -1, itemNames[i],
                itemDescriptions[i], 0, itemPrices[i], locations[i], coverShipping[i],
                coveredShipping[i], this))
        }


        val recyclerview = findViewById<RecyclerView>(R.id.searchRecycler)

        recyclerview.layoutManager = LinearLayoutManager(this)

        val adapter = WantAdAdapter(data)

        recyclerview.adapter = adapter
    }

    private val items = arrayOf("Worldwide", "Your State", "Your City")
    var checkedItem = 0

    private fun showFilterAlert()
    {
        var newItem = -1

        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
        .setTitle("Filter Search")

        .setSingleChoiceItems(
            items, checkedItem
        ) { dialog, which ->
            when (which) {
                0 -> newItem = 0
                1 -> newItem = 1
                2 -> newItem = 2
            }
        }

        .setPositiveButton("Apply"
        ) { dialog, _ ->
            if(newItem >= 0)
                checkedItem = newItem
            //TODO: Save filter here (Not sure if this solution is valid)
            dialog.cancel()
        }
        .setNegativeButton("Cancel"
                ) { dialog, _ ->
            dialog.cancel()
        }

        val alert: AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(true)
        alert.show()
    }

}