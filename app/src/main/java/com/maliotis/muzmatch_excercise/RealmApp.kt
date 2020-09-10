package com.maliotis.muzmatch_excercise

import android.app.Application
import io.realm.Realm

/**
 * Created by petrosmaliotis on 10/09/2020.
 */
public class RealmApp: Application() {

    override fun onCreate() {
        super.onCreate()
        // Init once
        Realm.init(this)
    }
}