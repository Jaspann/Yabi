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
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, NavigationBarView.OnItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        setSupportActionBar(toolbar)
        nav_view.setNavigationItemSelectedListener(this)
        bottomBar.setOnItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)

        val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)

        setRefreshRecyclers()

        if(sharedPreferences.getBoolean("buyerOnly", false))
        {
            bottomBar.visibility = View.GONE

            YourPostsRecycler.visibility = View.VISIBLE
            refreshYourPosts.visibility = View.VISIBLE
        }
        else
        {
            forYouRecycler.visibility = View.VISIBLE
            refreshForYou.visibility = View.VISIBLE
        }

    }

    private fun setRefreshRecyclers()
    {
        refreshForYou.setOnRefreshListener(OnRefreshListener {
            refreshForYou.isRefreshing = false
            getData()
        })
        refreshLocal.setOnRefreshListener(OnRefreshListener {
            refreshLocal.isRefreshing = false
            getData()
        })
        refreshNewPosts.setOnRefreshListener(OnRefreshListener {
            refreshNewPosts.isRefreshing = false
            getData()
        })
        refreshYourPosts.setOnRefreshListener(OnRefreshListener {
            refreshYourPosts.isRefreshing = false
            //fillYourPosts()
        })
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {

        super.onPostCreate(savedInstanceState)

        getData()
    }

    private fun getData(){

        val db = Firebase.firestore
        var queryResult: MutableList<DocumentSnapshot>
        val userID = intent.getStringExtra("userID")

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
                val images = arrayListOf<String>()
                for (document in queryResult) {
                    try {
                        if (document.get("userID") != userID) {
                            itemNames.add(document.get("itemName") as String)
                            itemDescriptions.add(document.get("itemDescription") as String)
                            tempLong =
                                document.get("requestedPrice").toString().substringBefore('.')
                                    .toLong()
                            itemPrices.add(tempLong.toDouble())
                            locations.add(
                                document.get("shippingCity") as String + ", " + document.get(
                                    "shippingState"
                                ) as String
                            )
                            coverShipping.add(document.get("coverShipping") as Boolean)
                            tempLongTwo =
                                document.get("coveredShipping").toString().substringBefore('.')
                                    .toLong()
                            coveredShipping.add(tempLongTwo.toDouble())
                            if(document.contains("imagePath"))
                                images.add(document.get("imagePath") as String)
                            else
                                images.add("")
                        }
                    } catch(e: NullPointerException) {
                        Log.e("MainActivity", "Error processing listings", e)
                    } catch(e: ClassCastException) {
                        Log.e("MainActivity", "Error casting listing types", e)
                    }
                }

                fillHome(itemNames, itemDescriptions, itemPrices, locations, coverShipping, coveredShipping, images)
            }
            .addOnFailureListener{ e ->
                Log.w("TAG", "Failed to retrieve listings.", e)
            }


        fillYourPosts()
    }

    //TODO Make listing class and pass array of listings
    private fun fillYourPosts()
    {
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

    }

    private fun fillHome(itemNames: List<String>, itemDescriptions: List<String>,
                         itemPrices: List<Double>, locations: List<String>,
                         coverShipping: List<Boolean>, coveredShipping: List<Double>, images: List<String>)
    {
        val data = ArrayList<WantAdViewModel>()

        for (i in itemNames.indices) {
            data.add(WantAdViewModel("User", -1, itemNames[i],
                itemDescriptions[i], 0, itemPrices[i], locations[i], coverShipping[i],
                coveredShipping[i], images[i], this))
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

    override fun onResume() {
        val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)

        if(sharedPreferences.getBoolean("buyerOnly", false))
        {
            bottomBar.visibility = View.GONE
            hideScreens(R.id.your_posts)
        }
        else
        {
            bottomBar.visibility = View.VISIBLE
            hideScreens(bottomBar.selectedItemId)
        }

        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_nav_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)

        when (item.itemId) {
            R.id.search_button -> {
                val intent = Intent(this, Search::class.java)
                startActivity(intent)
            }
            R.id.add_post_button -> {
                if(sharedPreferences.getBoolean("isGuest", false))
                {
                    Toast.makeText(
                        applicationContext,
                        "Please sign in first",
                        Toast.LENGTH_LONG
                    ).show()
                }
                else {
                    val intent = Intent(this, AddPost::class.java)
                    val userID = this.intent.getStringExtra("userID")
                    intent.putExtra("userID", userID)
                    startActivity(intent)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)

        if (item.itemId == R.id.local)
        {
            hideScreens(item.itemId)
        }
        else if (item.itemId == R.id.for_you)
        {
            hideScreens(item.itemId)
        }
        else if (item.itemId == R.id.new_postings)
        {
            hideScreens(item.itemId)
        }
        //change boolean expression to test things as guest
        else if(!sharedPreferences.getBoolean("isGuest", false))
        {
            when (item.itemId) {
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
                    val userID = this.intent.getStringExtra("userID")
                    intent.putExtra("userID", userID)
                    intent.putExtra("SignUp", false)
                    startActivity(intent)
                }
                R.id.your_posts -> {
                    hideScreens(item.itemId)
                }
            }
        }
        else
            Toast.makeText(
                applicationContext,
                "Please sign in first",
                Toast.LENGTH_LONG
            ).show()

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    //hides all recyclers except for argument screen. pass 0 to hide all
    private fun hideScreens(screen : Int)
    {
        forYouRecycler.visibility = View.GONE
        LocalRecycler.visibility = View.GONE
        NewPostsRecycler.visibility = View.GONE
        YourPostsRecycler.visibility = View.GONE
        refreshForYou.visibility = View.GONE
        refreshLocal.visibility = View.GONE
        refreshNewPosts.visibility = View.GONE
        refreshYourPosts.visibility = View.GONE

        when(screen) {
            R.id.for_you ->{
                forYouRecycler.visibility = View.VISIBLE
                refreshForYou.visibility = View.VISIBLE
            }
            R.id.local ->{
                LocalRecycler.visibility = View.VISIBLE
                refreshLocal.visibility = View.VISIBLE
            }
            R.id.new_postings ->{
                NewPostsRecycler.visibility = View.VISIBLE
                refreshNewPosts.visibility = View.VISIBLE
            }
            R.id.your_posts ->{
                YourPostsRecycler.visibility = View.VISIBLE
                refreshYourPosts.visibility = View.VISIBLE
            }
        }

    }
}