package com.maliotis.muzmatch_excercise.controller

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by petrosmaliotis on 11/09/2020.
 */
object Login {

    public fun withUser(username: String, callback: (String) -> Unit): Disposable {
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
}