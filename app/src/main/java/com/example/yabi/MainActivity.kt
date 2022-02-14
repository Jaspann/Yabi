package com.example.yabi

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView
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
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {

        super.onPostCreate(savedInstanceState)

        fillHome()

        fillYourPosts()
    }

    fun fillYourPosts()
    {

        val data = ArrayList<YourPostViewModel>()

        /*
        for (i in titles.indices) {
            data.add(YourPostViewModel(titles[i], desc[i], photos[i]))
        }
        */
        val recyclerview = findViewById<RecyclerView>(R.id.YourPostsRecycler)

        recyclerview.layoutManager = LinearLayoutManager(this)

        val adapter = YourPostAdapter(data)

        recyclerview.adapter = adapter
    }

    fun fillHome()
    {


        val data = ArrayList<WantAdViewModel>()
        /*
        for (i in users.indices) {
            data.add(WantAdViewModel(users[i], scores[i], titles[i], desc[i], photos[i]))
        }
        */

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