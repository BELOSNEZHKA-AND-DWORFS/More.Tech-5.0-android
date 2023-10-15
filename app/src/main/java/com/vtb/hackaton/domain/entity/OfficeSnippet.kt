package com.vtb.hackaton.domain.entity

data class OfficeSnippet (
    val headContent: String = "Банк ВТБ",
    val isAvailable: Boolean,
    val workingTime: String,
    val address: String = "",
    val distance: String,
    val phoneNumber: String = "+7-888-226-53-27",
    val coordsLongitude: String = "",
    val coordsLatitude: String = "",
)