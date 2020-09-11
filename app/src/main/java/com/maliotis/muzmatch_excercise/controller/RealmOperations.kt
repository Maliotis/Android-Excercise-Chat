package com.maliotis.muzmatch_excercise.controller

import com.maliotis.muzmatch_excercise.data.realmObjects.Channel
import com.maliotis.muzmatch_excercise.data.realmObjects.Content
import com.maliotis.muzmatch_excercise.data.realmObjects.Message
import com.maliotis.muzmatch_excercise.data.realmObjects.User
import io.realm.Realm
import java.util.*

/**
 * Created by petrosmaliotis on 10/09/2020.
 */
object RealmOperations {

    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    public fun createUserIfNotExists(username: String): String? {
        var user = realm.where(User::class.java).equalTo("name", username).findFirst()
        if (user == null) {
            user = User()
            user.id = UUID.randomUUID().toString()
            user.name = username
            realm.executeTransaction { r ->
                r.copyToRealmOrUpdate(user)
            }
        }
        return user.id
    }

    public fun createChannelIfNotExists(channelName: String): String? {
        var channel = realm.where(Channel::class.java).equalTo("name", channelName).findFirst()
        if (channel == null) {
            channel = Channel()
            channel.id = UUID.randomUUID().toString()
            channel.name = channelName
            realm.executeTransaction { r ->
                r.copyToRealmOrUpdate(channel)
            }
        }
        return channel.id
    }

    public fun addUserToChannel(userId: String, channelId: String): Boolean {
        var channel = realm.where(Channel::class.java).equalTo("id", channelId).findFirst()
        var user = realm.where(User::class.java).equalTo("name", userId).findFirst()

        if (user != null && channel != null) {
            if (!user.channels.contains(channel)) {
                user.channels.add(channel)
                channel.users.add(user)
            }
        }

        return true
    }

    public fun createContentWithText(text: String?): String? {
        val content = Content()
        content.id = UUID.randomUUID().toString()
        content.text = text
        realm.executeTransaction { r ->
            r.copyToRealmOrUpdate(content)
        }
        return content.id
    }

    public fun createMessageToChannelWithUser(contentId: String, channelId: String, userId: String): String? {
        val content = realm.where(Content::class.java).equalTo("id", contentId).findFirst()
        val user = realm.where(User::class.java).equalTo("id", userId).findFirst()
        val channel = realm.where(Channel::class.java).equalTo("id", channelId).findFirst()

        val message = Message()
        message.id = UUID.randomUUID().toString()
        message.content = content
        message.user = user
        message.channel = channel
        realm.executeTransaction { r ->
            r.copyToRealmOrUpdate(message)
        }
        // Reverse relationships
        user?.messages?.add(message)
        channel?.messages?.add(message)
        return message.id
    }
}