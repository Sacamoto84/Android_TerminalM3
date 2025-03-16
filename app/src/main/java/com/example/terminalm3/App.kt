package com.example.terminalm3

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        //Sherlock.init(this) //Initializing Sherlock
    }

}




