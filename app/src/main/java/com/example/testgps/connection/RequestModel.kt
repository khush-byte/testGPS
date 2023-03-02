package com.example.testgps.connection

data class RequestModel(
    val sign: String,
    val datetime: String,
    val latitude: String,
    val longitude: String,
    val userId: String
)
