package com.maliotis.muzmatch_excercise.controller

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by petrosmaliotis on 11/09/2020.
 */
object Operations {

    public fun createUser(username: String, callback: (String) -> Unit): Disposable {
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

    public fun setChannel(channelName: String, callback: (String) -> Unit): Disposable {
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

    public fun addUserToChannel(userId: String, channelId: String, callback: (Boolean) -> Unit): Disposable {
        return Observable.create<Boolean> { emitter ->
            val succeeded = RealmOperations.addUserToChannel(userId, channelId)
            emitter.onNext(succeeded)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { succeeded ->
                callback(succeeded)
            }
    }

}