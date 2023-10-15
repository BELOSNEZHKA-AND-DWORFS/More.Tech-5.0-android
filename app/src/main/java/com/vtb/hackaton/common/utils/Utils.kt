package com.vtb.hackaton.common.utils

import android.view.View
import androidx.core.view.isVisible

fun <T : View, V> T.goneOrRun(value: V?, block: T.(V) -> Unit) {
    this.isVisible = value != null
    if (value != null) {
        this.block(value)
    }
}