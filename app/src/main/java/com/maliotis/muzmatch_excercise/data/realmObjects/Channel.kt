package com.maliotis.muzmatch_excercise.data.realmObjects

import io.realm.RealmList
import io.realm.RealmObject

/**
 * Created by petrosmaliotis on 10/09/2020.
 */
class Channel(
    var users: RealmList<User> = RealmList(),
    var messages: RealmList<Message> = RealmList()
): RealmObject()