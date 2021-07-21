package com.example.kotlinmessenger.models

class ChatMessage(
    val id: String,
    val text: String,
    val fromId: String,
    val ToId: String,
    val timeStamp: Long
) {
    constructor() : this("", "","","",-1)
}