package com.myYabi.yabi

import android.content.Context

data class YourPostViewModel(
    val listingID: String,
    val sellerID: String,
    val title : String,
    val desc : String,
    val img : Int,
    val offers : Int,
    val price : Double,
    val location : String,
    val shippingSeller : Boolean,
    val shipCover : Double,
    val image: String,
    val context : Context) {}
