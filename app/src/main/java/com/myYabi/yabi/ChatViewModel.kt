package com.myYabi.yabi

import android.content.Context

data class ChatViewModel(
    val account: String,
    val messages: String,
    val offers: Double,
    val context: Context){}
