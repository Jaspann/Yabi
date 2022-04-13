package com.myYabi.yabi

import android.content.Context

data class WantAdViewModel(
    val listingID: String,
    val username: String,
    val rating: Int,
    val title: String,
    val desc: String,
    val img: Int,
    val price: Double,
    val location: String,
    val shippingSeller: Boolean,
    val shipCover: Double,
    val tag: String,
    val image: String,
    val context: Context){}
