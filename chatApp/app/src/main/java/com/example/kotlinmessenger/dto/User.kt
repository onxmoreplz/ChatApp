package com.example.kotlinmessenger.dto

class User(
    val uid: String,
    val username: String,
    val profileImageUrl: String
) {
    constructor() : this("", "", "")
}