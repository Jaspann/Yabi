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
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
                //TODO: Enter Database query here, and send to fillSearch()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
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

    fun fillSearch(itemNames: List<String>, itemDescriptions: List<String>, itemPrices: List<Double>,
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

    private fun showFilterAlert()
    {
        val items = arrayOf("Worldwide", "Your State", "Your City")
        val checkedItem = 1

        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
        .setTitle("Filter Search")

        .setSingleChoiceItems(
            items, checkedItem
        ) { dialog, which ->
            when (which) {
                0 -> Toast.makeText(this, "Worldwide selected", Toast.LENGTH_LONG).show()
                1 -> Toast.makeText(this, "Your State selected", Toast.LENGTH_LONG).show()
                2 -> Toast.makeText(this, "Your City selected", Toast.LENGTH_LONG).show()
            }
        }

        .setPositiveButton("Apply"
        ) { dialog, _ ->
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