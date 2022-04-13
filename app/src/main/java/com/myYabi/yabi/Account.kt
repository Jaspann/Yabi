package com.myYabi.yabi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import myYabi.yabi.R
import kotlinx.android.synthetic.main.activity_main.*




class Account : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }
}