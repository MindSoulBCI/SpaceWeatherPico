package com.example.spatialweather.p10282026.platform

import android.app.Application
import com.pico.spatial.ui.foundation.dsl.launch
import com.example.spatialweather.p10282026.mainApp

class SpatialApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        launch(::mainApp)
    }
}
