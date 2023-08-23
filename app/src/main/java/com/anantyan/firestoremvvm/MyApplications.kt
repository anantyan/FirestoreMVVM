package com.anantyan.firestoremvvm

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestoreSettings
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplications : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(applicationContext)
        if (!BuildConfig.DEBUG) {
            val firebaseAppCheck = FirebaseAppCheck.getInstance()
            firebaseAppCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance())
        } else {
            val firebaseAppCheck = FirebaseAppCheck.getInstance()
            firebaseAppCheck.installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance())
        }
    }
}