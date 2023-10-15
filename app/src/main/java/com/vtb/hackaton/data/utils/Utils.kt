package com.vtb.hackaton.data.utils

fun <T> List<T>.takeIfNotEmpty(): List<T>? = takeIf { it.isNotEmpty() }