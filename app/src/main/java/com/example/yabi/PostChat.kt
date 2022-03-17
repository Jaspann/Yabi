package com.example.yabi

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.activity_post_chat.*


class PostChat : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_chat)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        getDatabaseMessages()

        userMessageInput.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN &&
                keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                //TODO: send user message to database

                userMessageInput.text = null
                return@OnKeyListener true
            }
            false
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.info_button -> {
                val intent = Intent(this, ChatPostInfo::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_nav_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun getDatabaseMessages()
    {
        //TODO Connect to real database, dummy value for now

        val offer = arrayOf(true, false, true, false, false, true, false, false)
        val account = arrayOf("them", "us", "us", "them", "them", "us", "us", "them")
        val title = arrayOf("Good quality Obj", "", "Good quality Obj", "", "",
            "Good quality Obj", "", "")
        val desc = arrayOf("I have a obj you want in good quality, and I can pay for shipping. I can ship it from Vermont.",
            "That sounds good, but 34 is too much, my original asking price was 10. But it is good quality, so can you do 24?",
            "obj in good quality, seller paying for shipping.",
            "24 is not worth it after shipping",
            "unless you take shipping instead?",
            "obj in good quality, buyer paying for shipping.",
            "Will this work for you?",
            "sounds good to me."
        )
        val price = arrayOf(34.00, -1.0, 24.00, -1.0, -1.0, 24.00, -1.0, -1.0)
        val locationTo = arrayOf("New York, NY", "", "New York, NY", "", "", "New York, NY", "", "")
        val locationFrom = arrayOf("Burlington, VM", "", "Burlington, VM", "", "", "Burlington, VM", "", "")
        val shippingSeller = arrayOf(true, false, true, false, false, false, false, false)
        val covering = arrayOf(-1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0)

        fillChatMsg(offer, account, title, desc, price, locationTo, locationFrom, shippingSeller, covering)


    }

    private fun fillChatMsg(offers: Array<Boolean>, accounts: Array<String>, titles: Array<String>,
                            descriptions: Array<String>, prices: Array<Double>, locationTo: Array<String>,
                            locationFrom: Array<String>, shippingSeller: Array<Boolean>, covering: Array<Double>)
    {
        val data = ArrayList<ChatViewModel>()

        for (i in offers.indices) {
            data.add(ChatViewModel(offers[i], accounts[i], titles[i], descriptions[i], 0,
                prices[i], locationFrom[i], locationTo[i], shippingSeller[i], covering[i], this))
        }
        val recyclerview = findViewById<RecyclerView>(R.id.chatRecycler)

        recyclerview.layoutManager = LinearLayoutManager(this)

        val adapter = ChatAdapter(this, offers, accounts, titles, descriptions, prices,
            locationTo, locationFrom, shippingSeller, covering)

        recyclerview.adapter = adapter
    }

    fun onPressCreateOffer(view: View)
    {
        val intent = Intent(this, AddPost::class.java)
        intent.putExtra("isCounter", true)
        startActivity(intent)
    }
}