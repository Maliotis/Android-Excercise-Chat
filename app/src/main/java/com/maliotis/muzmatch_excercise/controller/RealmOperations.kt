package com.maliotis.muzmatch_excercise.controller

import android.util.Log
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

    val TAG = RealmOperations::class.java.simpleName

    fun createUserIfNotExists(username: String): String? {
        val realm = Realm.getDefaultInstance()
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

    fun createChannelIfNotExists(channelName: String): String? {
        val realm = Realm.getDefaultInstance()
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

    fun addUserToChannel(userId: String, channelId: String): Boolean {
        val realm = Realm.getDefaultInstance()
        val channel = realm.where(Channel::class.java).equalTo("id", channelId).findFirst()
        val user = realm.where(User::class.java).equalTo("id", userId).findFirst()

        var succeeded = true

        Log.d(TAG, "addUserToChannel: channel = $channel")
        Log.d(TAG, "addUserToChannel: user = $user")
        realm.executeTransaction {
            if (user != null && channel != null) {
                if (!user.channels.contains(channel)) {
                    user.channels.add(channel)
                    channel.users.add(user)
                }
            } else {
                succeeded =  false
            }

        }
        return succeeded
    }

    fun createContentWithText(text: String?): String? {
        val realm = Realm.getDefaultInstance()
        val content = Content()
        content.id = UUID.randomUUID().toString()
        content.text = text
        realm.executeTransaction { r ->
            r.copyToRealmOrUpdate(content)
        }
        return content.id
    }

    fun createMessageToChannelWithUser(contentId: String, channelId: String, userId: String): String? {
        val realm = Realm.getDefaultInstance()
        val content = realm.where(Content::class.java).equalTo("id", contentId).findFirst()
        val user = realm.where(User::class.java).equalTo("id", userId).findFirst()
        val channel = realm.where(Channel::class.java).equalTo("id", channelId).findFirst()

        val message = Message()
        message.id = UUID.randomUUID().toString()
        message.content = content
        message.user = user
        message.time = System.currentTimeMillis()
        message.channel = channel
        message.alpha = 0f
        message.tail = true
        realm.executeTransaction { r ->
            r.copyToRealmOrUpdate(message)
        }

        // Reverse relationships
        realm.executeTransaction {
            user?.messages?.add(message)
            channel?.messages?.add(message)
        }
        return message.id
    }

    // UI Realm operations

    fun getAllMessagesForChannel(channelId: String): MutableList<Message>? {
        val uiRealm = Realm.getDefaultInstance()
        val channel = uiRealm.where(Channel::class.java).equalTo("id", channelId).findFirst()
        return channel?.messages?.toMutableList()
    }

    fun getChannel(channelId: String): Channel? {
        val uiRealm = Realm.getDefaultInstance()
        return uiRealm.where(Channel::class.java).equalTo("id", channelId).findFirst()
    }

    fun getMessage(messageId: String): Message? {
        val uiRealm = Realm.getDefaultInstance()
        return uiRealm.where(Message::class.java).equalTo("id", messageId).findFirst()
    }

    fun changeAlphaOnMessage(messageId: String, alphaValue: Float) {
        val uiRealm = Realm.getDefaultInstance()
        val message = uiRealm.where(Message::class.java).equalTo("id", messageId).findFirst()
        uiRealm.executeTransaction {
            message?.alpha = alphaValue
        }
    }

    fun changeTailOnMessage(messageId: String, hasTail: Boolean) {
        val uiRealm = Realm.getDefaultInstance()
        val message = uiRealm.where(Message::class.java).equalTo("id", messageId).findFirst()
        uiRealm.executeTransaction {
            message?.tail = hasTail
        }
    }

    fun getUser(userId: String): User? {
        val uiRealm = Realm.getDefaultInstance()
        return uiRealm.where(User::class.java).equalTo("id", userId).findFirst()

    }
}