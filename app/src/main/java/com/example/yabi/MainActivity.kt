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
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.activity_settings.*


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
            getNewPosts()
        })
        refreshYourPosts.setOnRefreshListener(OnRefreshListener {
            refreshYourPosts.isRefreshing = false
            //fillYourPosts()
        })
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {

        super.onPostCreate(savedInstanceState)

        getNewPosts()
        getData()
    }

    private fun getNewPosts(){
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
                val tags = arrayListOf<String>()
                val images = arrayListOf<String>()
                val coverShipping = arrayListOf<Boolean>()
                val coveredShipping = arrayListOf<Double>()
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
                            if(document.contains("tag"))
                                tags.add(document.get("tag") as String)
                            else
                                tags.add("")
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

                fillNewPosts(itemNames, itemDescriptions, itemPrices, locations, tags, coverShipping, coveredShipping, images)
            }
            .addOnFailureListener{ e ->
                Log.w("TAG", "Failed to retrieve listings.", e)
            }

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
                val tags = arrayListOf<String>()
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
                            if(document.contains("tag"))
                                tags.add(document.get("tag") as String)
                            else
                                tags.add("")
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


                val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)

                if(
                    sharedPreferences.getBoolean("Furniture", false) ||
                    sharedPreferences.getBoolean("Games", false) ||
                    sharedPreferences.getBoolean("Cards", false) ||
                    sharedPreferences.getBoolean("Paintings", false) ||
                    sharedPreferences.getBoolean("Clothing", false) ||
                    sharedPreferences.getBoolean("Home Improvement", false) ||
                    sharedPreferences.getBoolean("Accessory", false) ||
                    sharedPreferences.getBoolean("Collectable", false)
                ) {
                    var index = 0
                    while (index < itemNames.size) {
                        val hasTag = sharedPreferences.getBoolean(tags[index], false)
                        if (!hasTag) {
                            itemNames.removeAt(index)
                            itemDescriptions.removeAt(index)
                            itemPrices.removeAt(index)
                            locations.removeAt(index)
                            tags.removeAt(index)
                            coverShipping.removeAt(index)
                            coveredShipping.removeAt(index)
                            images.removeAt(index)
                        } else
                            index++
                    }
                }

                fillHome(itemNames, itemDescriptions, itemPrices, locations, tags, coverShipping, coveredShipping, images)
            }
            .addOnFailureListener{ e ->
                Log.w("TAG", "Failed to retrieve listings.", e)
            }


        fillYourPosts()
    }

    //TODO Make listing class and pass array of listings
    private fun fillYourPosts()
    {




    }
    private fun fillHome(itemNames: List<String>, itemDescriptions: List<String>,
                             itemPrices: List<Double>, locations: List<String>, tags: List<String>,
                             coverShipping: List<Boolean>, coveredShipping: List<Double>, images: List<String>){
        val data = ArrayList<WantAdViewModel>()

        //val photos = arrayOf(0)

        for (i in itemNames.indices) {
            data.add(WantAdViewModel("User", -1, itemNames[i],
                itemDescriptions[i], 0, itemPrices[i], locations[i], coverShipping[i],
                coveredShipping[i], tags[i], images[i], this))
        }


        val recyclerview = findViewById<RecyclerView>(R.id.forYouRecycler)

        recyclerview.layoutManager = LinearLayoutManager(this)

        val adapter = WantAdAdapter(data)

        recyclerview.adapter = adapter
    }


    private fun fillNewPosts(itemNames: List<String>, itemDescriptions: List<String>,
                         itemPrices: List<Double>, locations: List<String>, tags: List<String>,
                         coverShipping: List<Boolean>, coveredShipping: List<Double>, images: List<String>)
    {
        val data = ArrayList<WantAdViewModel>()

        for (i in itemNames.indices) {
            data.add(WantAdViewModel("User", -1, itemNames[i],
                itemDescriptions[i], 0, itemPrices[i], locations[i], coverShipping[i],
                coveredShipping[i], tags[i], images[i], this))
        }


        val recyclerview = findViewById<RecyclerView>(R.id.NewPostsRecycler)

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
                if(sharedPreferences.getString("emailAddress", "") == "")
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
        else if(sharedPreferences.getString("emailAddress", "") != "")
        {
            when (item.itemId) {
                /*
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
                 */
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

/*
From activity_main_drawer.xml.
We might not get to these options and did not want to leave blank at this time.

        <item
            android:id="@+id/nav_account"
            android:icon="@drawable/ic_baseline_account_circle_24"
            android:title="@string/account" />
        <item
            android:id="@+id/nav_chat"
            android:icon="@drawable/ic_baseline_message_24"
            android:title="@string/chats" />
        <item
            android:id="@+id/nav_posts"
            android:icon="@drawable/ic_baseline_trip_origin_24"
            android:title="@string/your_posts" />
        <item
            android:id="@+id/nav_complete"
            android:icon="@drawable/ic_baseline_repeat_24"
            android:title="@string/completed_offers"/>
 */