package com.maliotis.muzmatch_excercise.data.realmObjects

import io.realm.RealmObject
import io.realm.annotations.Required

/**
 * Created by petrosmaliotis on 10/09/2020.
 */
open class Message(
    @Required
    var user: User? = null,
    @Required
    var time: Long? = null,
    @Required
    var content: Content? = null
): RealmObject()

