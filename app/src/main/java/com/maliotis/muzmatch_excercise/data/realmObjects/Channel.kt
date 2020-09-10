package com.maliotis.muzmatch_excercise.data.realmObjects

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by petrosmaliotis on 10/09/2020.
 */
class Channel(
    @PrimaryKey
    var id: String? = null,
    var users: RealmList<User> = RealmList(),
    var messages: RealmList<Message> = RealmList()
): RealmObject()