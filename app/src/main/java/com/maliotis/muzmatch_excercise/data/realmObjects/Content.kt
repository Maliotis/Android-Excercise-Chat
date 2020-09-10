package com.maliotis.muzmatch_excercise.data.realmObjects

import io.realm.RealmObject

/**
 * Created by petrosmaliotis on 10/09/2020.
 */
open class Content(
    var text: String? = null,
    var sticker: String? = null
): RealmObject()