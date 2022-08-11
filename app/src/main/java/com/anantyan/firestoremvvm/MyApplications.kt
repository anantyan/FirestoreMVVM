package com.anantyan.firestoremvvm

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplications : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(applicationContext)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance())
    }
}