package com.example.wastemanagement

import android.app.Application
import com.cloudinary.android.MediaManager
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WasteManagementApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )

        // Cloudinary configuration
        val config = mapOf(
            "cloud_name" to "dvqcumuuj",
            "api_key" to "148255823821593",
            "api_secret" to "dQY9V9_1B0_AiYkbH6va79Srrkc"
        )
        MediaManager.init(this, config)
    }
}
