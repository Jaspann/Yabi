package com.example.yabi

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.activity_search.*
import java.lang.NumberFormatException

class Search : AppCompatActivity() {

    val TAG: String = "Search"

    var requestedPriceMin: Double = 0.0
    var requestedPriceMax: Double = 999999.0
    var searchTerm: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchTerm = searchView.query.toString()
                searchListings()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        EditTextPriceMin.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                try {
                    requestedPriceMin = p0.toString().toDouble()
                    searchListings()
                } catch (e: NumberFormatException) {
                    Toast.makeText(
                        this@Search,
                        "Invalid text. Only numbers allowed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })

        EditTextPriceMax.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                try {
                    requestedPriceMax = p0.toString().toDouble()
                    searchListings()
                } catch (e: NumberFormatException) {
                    Toast.makeText(
                        this@Search,
                        "Invalid text. Only numbers allowed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })

        refreshSearch.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            refreshSearch.isRefreshing = false
            searchListings()
        })
    }

    private fun searchListings()
    {
        //TODO: call appropriate function, currently has temp data
        val db = Firebase.firestore
        var queryResult = mutableListOf<DocumentSnapshot>()



        db.collection("listings")
            .whereGreaterThanOrEqualTo("requestedPrice", requestedPriceMin)
            .whereLessThanOrEqualTo("requestedPrice", requestedPriceMax)
            .orderBy("requestedPrice", Query.Direction.DESCENDING)
            //.orderBy("creationTimestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { results ->
                Log.d(TAG, "Listings retrieved.")
                for (document in results) {
                    if (searchTerm != "") {
                        val isMatch = document.get("itemName").toString().contains(searchTerm, true)
                        if (isMatch) {
                            queryResult.add(document)
                        }
                    } else {
                        queryResult.add(document)
                    }
                }
                //TODO: Find permanent solution to workaround in assignment of longs
                var tempLong: Long
                var tempLongTwo: Long
                val itemNames = arrayListOf<String>()
                val itemDescriptions = arrayListOf<String>()
                val itemPrices = arrayListOf<Double>()
                val locations = arrayListOf<String>()
                val tags = arrayListOf<String>()
                val coverShipping = arrayListOf<Boolean>()
                val coveredShipping = arrayListOf<Double>()
                for (document in queryResult) {
                    try {
                        itemNames.add(document.get("itemName") as String)
                        itemDescriptions.add(document.get("itemDescription") as String)
                        tempLong = document.get("requestedPrice").toString().substringBefore('.').toLong() as Long
                        itemPrices.add(tempLong.toDouble())
                        locations.add(document.get("shippingCity") as String + ", " + document.get("shippingState") as String)
                        if(document.contains("tag"))
                            tags.add(document.get("tag") as String)
                        else
                            tags.add("")
                        coverShipping.add(document.get("coverShipping") as Boolean)
                        tempLongTwo = document.get("coveredShipping").toString().substringBefore('.').toLong() as Long
                        coveredShipping.add(tempLongTwo.toDouble())
                    } catch(e: NullPointerException) {
                        Log.e(TAG, "Error processing listings", e)
                    } catch(e: ClassCastException) {
                        Log.e(TAG, "Error casting listing types", e)
                    }
                }
                fillSearch(itemNames, itemDescriptions, itemPrices, locations, tags, coverShipping, coveredShipping)
            }
            .addOnFailureListener{ e ->
                Log.w(TAG, "Failed to retrieve listings.", e)
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_nav_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun fillSearch(itemNames: List<String>, itemDescriptions: List<String>,
                           itemPrices: List<Double>, locations: List<String>,
                           tags: List<String>, coverShipping: List<Boolean>, coveredShipping: List<Double>)
    {
        val data = ArrayList<WantAdViewModel>()

        //val photos = arrayOf(0)

        for (i in itemNames.indices) {
            data.add(WantAdViewModel("User", -1, itemNames[i],
                itemDescriptions[i], 0, itemPrices[i], locations[i], coverShipping[i],
                coveredShipping[i], tags[i], this))
        }


        val recyclerview = findViewById<RecyclerView>(R.id.searchRecycler)

        recyclerview.layoutManager = LinearLayoutManager(this)

        val adapter = WantAdAdapter(data)

        recyclerview.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.filter_location -> {
                showFilterLocationAlert()
            }
            R.id.filter_shipping -> {
                showFilterShippingAlert()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val shippingItems = arrayOf("All", "Seller", "Buyer")
    private var checkedShippingItem = 0

    private fun showFilterShippingAlert(){
        var newItem = -1

        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
            .setTitle("Filter by Shipping")

            .setSingleChoiceItems(
                shippingItems, checkedShippingItem
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
                    checkedShippingItem = newItem
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

    private val locationItems = arrayOf("Worldwide", "State", "City")
    private var checkedLocationItem = 0

    private fun showFilterLocationAlert() {
        var newItem = -1

        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
        .setTitle("Filter by Location")

        .setSingleChoiceItems(
            locationItems, checkedLocationItem
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
                checkedLocationItem = newItem
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