package com.example.yabi

import android.content.Context

data class ChatViewModel(
    val offer: Boolean,
    val account: String,
    val title: String,
    val desc: String,
    val img: Int,
    val price: Double,
    val locationFrom: String,
    val locationTo: String,
    val shippingSeller: Boolean,
    val shipCover: Double,
    val context: Context){}
