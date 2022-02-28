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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, NavigationBarView.OnItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        nav_view.setNavigationItemSelectedListener(this)
        bottomBar.setOnItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)

        if(intent.hasExtra("SignUp"))
            if(intent.getBooleanExtra("SignUp", false))
            {
                val intent = Intent(this, Settings::class.java)
                startActivity(intent)
            }

    }

    override fun onPostCreate(savedInstanceState: Bundle?) {

        super.onPostCreate(savedInstanceState)

        val db = Firebase.firestore
        var queryResult: MutableList<DocumentSnapshot>

        db.collection("listings")
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

                fillHome(itemNames, itemDescriptions, itemPrices, locations, coverShipping, coveredShipping)
            }
            .addOnFailureListener{ e ->
                Log.w("TAG", "Failed to retrieve listings.", e)
            }


        fillYourPosts()
    }

    //TODO Make listing class and pass array of listings
    private fun fillYourPosts()
    {/*
        val data = ArrayList<YourPostViewModel>()

        // Used For testing, remove when implementing database
        val titles = arrayOf("Obj I want")
        val desc = arrayOf("If anyone has this object, please reach out.")
        val photos = arrayOf(0)
        val offers = arrayOf(1)

        for (i in titles.indices) {
            data.add(YourPostViewModel(titles[i], desc[i], photos[0], offers[0], 10.00, "New York, NY", false, -1.0, this))
        }
        val recyclerview = findViewById<RecyclerView>(R.id.YourPostsRecycler)

        recyclerview.layoutManager = LinearLayoutManager(this)

        val adapter = YourPostAdapter(data)

        recyclerview.adapter = adapter
        */
    }

    fun fillHome(itemNames: List<String>, itemDescriptions: List<String>, itemPrices: List<Double>,
                 locations: List<String>, coverShipping: List<Boolean>, coveredShipping: List<Double>)
    {
        val data = ArrayList<WantAdViewModel>()

        //val photos = arrayOf(0)

        for (i in itemNames.indices) {
            data.add(WantAdViewModel("User", -1, itemNames[i],
                itemDescriptions[i], 0, itemPrices[i], locations[i], coverShipping[i],
                coveredShipping[i], this))
        }


        val recyclerview = findViewById<RecyclerView>(R.id.forYouRecycler)

        recyclerview.layoutManager = LinearLayoutManager(this)

        val adapter = WantAdAdapter(data)

        recyclerview.adapter = adapter
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            moveTaskToBack(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_nav_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search_button -> {
                val intent = Intent(this, Search::class.java)
                startActivity(intent)
            }
            R.id.add_post_button -> {
                val intent = Intent(this, AddPost::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                Toast.makeText(
                    applicationContext,
                    "Already on Home Page",
                    Toast.LENGTH_SHORT
                ).show()
            }
            R.id.nav_account -> {
                val intent = Intent(this, Account::class.java)
                startActivity(intent)
            }
            R.id.nav_chat -> {
                val intent = Intent(this, Messaging::class.java)
                startActivity(intent)
            }
            R.id.nav_posts -> {
                val intent = Intent(this, YourPosts::class.java)
                startActivity(intent)
            }
            R.id.nav_complete -> {
                val intent = Intent(this, CompletedOffers::class.java)
                startActivity(intent)
            }
            R.id.nav_settings -> {
                val intent = Intent(this, Settings::class.java)
                startActivity(intent)
            }
            R.id.for_you -> {
                forYouRecycler.visibility = View.VISIBLE
                LocalRecycler.visibility = View.GONE
                NewPostsRecycler.visibility = View.GONE
                YourPostsRecycler.visibility = View.GONE

            }
            R.id.local -> {
                forYouRecycler.visibility = View.GONE
                LocalRecycler.visibility = View.VISIBLE
                NewPostsRecycler.visibility = View.GONE
                YourPostsRecycler.visibility = View.GONE

            }
            R.id.new_postings -> {
                forYouRecycler.visibility = View.GONE
                LocalRecycler.visibility = View.GONE
                NewPostsRecycler.visibility = View.VISIBLE
                YourPostsRecycler.visibility = View.GONE

            }
            R.id.your_posts -> {
                forYouRecycler.visibility = View.GONE
                LocalRecycler.visibility = View.GONE
                NewPostsRecycler.visibility = View.GONE
                YourPostsRecycler.visibility = View.VISIBLE
                //toolbar.menu.getItem(R.id.add_post_button).isVisible = true

            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}