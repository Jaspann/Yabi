package com.example.yabi

import android.content.Context

data class WantAdViewModel(
    val username: String,
    val rating: Int,
    val title: String,
    val desc: String,
    val img: Int,
    val price: Double,
    val location: String,
    val shippingSeller: Boolean,
    val shipCover: Double,
    val image: String,
    val context: Context){}
