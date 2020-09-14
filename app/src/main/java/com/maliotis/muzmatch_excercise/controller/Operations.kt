package com.maliotis.muzmatch_excercise.controller

import com.maliotis.muzmatch_excercise.data.realmObjects.Channel
import com.maliotis.muzmatch_excercise.data.realmObjects.Message
import com.maliotis.muzmatch_excercise.data.realmObjects.User
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by petrosmaliotis on 11/09/2020.
 */
object Operations {

    /**
     * Creates user in a background thread and return userId in the UI async
     */
    fun createUser(username: String, callback: (String) -> Unit): Disposable {
        return Observable.create<String> { emitter ->
            val userId = RealmOperations.createUserIfNotExists(username)
            emitter.onNext(userId ?: "")
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { userId ->
                callback(userId)
            }
    }

    fun loginWithUser(username: String, callback: (String) -> Unit): Disposable {
        val disposable = Observable.create<String> { emitter ->
            val userId = RealmOperations.createUserIfNotExists(username)
            emitter.onNext(userId ?: "")
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { userId ->
                callback(userId)
            }

        return disposable
    }

    /**
     * Creates channel in a background thread and return channelId in the UI async
     */
    fun createChannel(channelName: String, callback: (String) -> Unit): Disposable {
        return Observable.create<String> { emmiter ->
            val channelId = RealmOperations.createChannelIfNotExists(channelName)
            emmiter.onNext(channelId ?: "")
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { channelId ->
                callback(channelId)
            }
    }

    /**
     * Add user to a channel in a background thread and returns isSuccessful in the UI async
     */
    fun addUserToChannel(userId: String, channelId: String, callback: (Boolean) -> Unit): Disposable {
        return Observable.create<Boolean> { emitter ->
            val succeeded = RealmOperations.addUserToChannel(userId, channelId)
            emitter.onNext(succeeded)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { isSuccessful ->
                callback(isSuccessful)
            }
    }

    /**
     * Sends a message with a text from a user in the specified channel
     * in a background thread and returns the messageId
     */
    fun sendMessageFrom(text: String, from: String, channel: String): String? {
        val contentId = RealmOperations.createContentWithText(text)
        if (contentId != null) {
            val messageId = RealmOperations.createMessageToChannelWithUser(contentId, channel, from)
            if (messageId != null)
                return messageId
        }
        return null
    }

    /**
     * Stub.
     *
     * Will be implemented as a feature in the future
     */
     fun sendStickerFrom(sticker: String, from: String, channel: String) {

    }

    fun getAllMessagesForChannelOnCurrentThread(channelId: String): MutableList<Message>? {
        return RealmOperations.getAllMessagesForChannel(channelId)
    }

    fun getChannelOnCurrentThread(channelId: String): Channel? {
        return RealmOperations.getChannel(channelId)
    }

    fun getMessageOnCurrentThread(messageId: String): Message? {
        return RealmOperations.getMessage(messageId)
    }

    fun changeAlphaOnMessageOnCurrentThread(messageId: String, alphaValue: Float) {
        RealmOperations.changeAlphaOnMessage(messageId, alphaValue)
    }

    fun changeTailOnMessageOnCurrentThread(messageId: String, hasTail: Boolean) {
        RealmOperations.changeTailOnMessage(messageId, hasTail)
    }

    fun getUserOnCurrentThread(userId: String): User? {
        return RealmOperations.getUser(userId)
    }

}