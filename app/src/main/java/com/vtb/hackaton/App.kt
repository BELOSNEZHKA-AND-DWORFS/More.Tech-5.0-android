package com.vtb.hackaton

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import com.yandex.runtime.i18n.I18nManagerFactory
import java.util.Locale

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
    }
}