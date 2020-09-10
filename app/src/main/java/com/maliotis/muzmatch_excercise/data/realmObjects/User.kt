package com.maliotis.muzmatch_excercise.data.realmObjects

import io.realm.RealmObject
import io.realm.annotations.Required

/**
 * Created by petrosmaliotis on 10/09/2020.
 */
open class User: RealmObject() {
    @Required
    var name: String = ""

}