package com.example.yabi

import android.content.Context

data class ChatViewModel(
    val account: String,
    val messages: String,
    val context: Context){}
