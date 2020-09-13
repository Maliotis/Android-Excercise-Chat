package com.maliotis.muzmatch_excercise.data.realmObjects

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

/**
 * Created by petrosmaliotis on 10/09/2020.
 */
open class Message(
    @PrimaryKey
    var id: String? = null,
    var time: Long? = null,
    var user: User? = null,
    var content: Content? = null,
    var channel: Channel? = null,
    var alpha: Float? = null,
    var tail: Boolean? = null
): RealmObject()

