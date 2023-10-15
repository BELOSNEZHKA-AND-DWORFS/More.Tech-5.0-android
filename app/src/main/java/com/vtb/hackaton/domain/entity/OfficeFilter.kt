package com.vtb.hackaton.domain.entity

data class OfficeFilter (
    val specific: String,
    val key: String = "",
    val isAvailable: Boolean = true,
    val textDescription: String = "",
)