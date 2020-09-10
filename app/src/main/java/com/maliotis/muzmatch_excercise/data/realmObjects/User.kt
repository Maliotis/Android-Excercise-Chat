package com.maliotis.muzmatch_excercise.data.realmObjects

import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

/**
 * Created by petrosmaliotis on 10/09/2020.
 */
open class User(
    @PrimaryKey
    var id: String? = null,
    var name: String = "",
    var channels: RealmList<Channel> = RealmList(),
    var messages: RealmList<Message> = RealmList()
): RealmObject()