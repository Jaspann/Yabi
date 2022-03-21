package com.example.yabi

import  android.content.Context

data class OfferViewModel (
    val buyer: String,
    val title : String,
    val offer: Double,
    val context:Context){}