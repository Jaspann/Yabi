package com.myYabi.yabi

import  android.content.Context

data class OfferViewModel (
    val buyer: String,
    val itemName : String,
    val yourOffer: Double,
    val context:Context){}