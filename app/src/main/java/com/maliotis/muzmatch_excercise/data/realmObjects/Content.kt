package com.maliotis.muzmatch_excercise.data.realmObjects

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by petrosmaliotis on 10/09/2020.
 */
open class Content(
    @PrimaryKey
    var id: String? = null,
    var text: String? = null,
    var sticker: String? = null
): RealmObject()