package com.anantyan.firestoremvvm.utils

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.anantyan.firestoremvvm.R
import com.google.android.material.snackbar.Snackbar

fun Context.onSnackError(view: View, message: String) {
    val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
    snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.error))
    snackbar.show()
}

fun Context.onSnackSuccess(view: View, message: String) {
    val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
    snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.success))
    snackbar.show()
}

fun Context.onToast(message: String) {
    val toast = Toast.makeText(this, message, Toast.LENGTH_LONG)
    toast.setGravity(Gravity.CENTER, 0, 0)
    toast.show()
}