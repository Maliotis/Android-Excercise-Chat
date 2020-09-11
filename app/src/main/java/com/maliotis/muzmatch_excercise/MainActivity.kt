package com.maliotis.muzmatch_excercise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.maliotis.muzmatch_excercise.controller.Operations
import com.maliotis.muzmatch_excercise.fragments.ChatFragment
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class MainActivity : AppCompatActivity() {

    // Realm Ids
    lateinit var loggedInUserId: String
    lateinit var channelId: String
    var bobIdSubject = PublishSubject.create<String>()
    var aliceIdSubject = PublishSubject.create<String>()
    var channelIdSubject = PublishSubject.create<String>()

    // Usernames
    var bob = "Bob"
    var alice = "Alice"

    // Defaults
    var defaultUser =  bob
    var defaultChannelName = "Bob Alice Channel"


    // Disposable List
    private val disposables = mutableListOf<Disposable>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addUsersToChannelObserver()

        val disposableDefaultUser = Operations.createUser(defaultUser) { userId ->
            loggedInUserId = userId
            bobIdSubject.onNext(userId)
        }

        val disposableAlice = Operations.createUser(alice) { userId ->
            print("Alice's id = $userId")
            aliceIdSubject.onNext(userId)
        }


        val disposableChannel = Operations.setChannel(defaultChannelName) { cid ->
            channelId = cid
            channelIdSubject.onNext(cid)
        }

        disposables.add(disposableDefaultUser)
        disposables.add(disposableAlice)
        disposables.add(disposableChannel)

        val chatFragment = ChatFragment()
        chatFragment.mainActivity = this
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, chatFragment)
            .commit()
    }

    private fun addUsersToChannelObserver() {
        val disposable = Observables.zip(bobIdSubject, aliceIdSubject, channelIdSubject) { bobId, aliceId, channelId ->
            mutableListOf(bobId, aliceId, channelId)
        }
            .observeOn(Schedulers.io())
            .flatMap { usersChannelId ->
                Observable.create<MutableList<String>> { emitter ->
                    // Add Bob to channel
                    val bobId = usersChannelId.first()
                    val aliceId = usersChannelId[1]
                    val channelId = usersChannelId[2]
                    Operations.addUserToChannel(bobId, channelId) { succeeded ->
                        if (succeeded) {
                            print("Bob added to channel withId = $channelId")
                            emitter.onNext(usersChannelId)
                        }
                    }
                }
            }
            .flatMap { usersChannelId ->
                Observable.create<Boolean> { emitter ->
                    // Add Alice to channel
                    val aliceId = usersChannelId[1]
                    val channelId = usersChannelId[2]
                    Operations.addUserToChannel(aliceId, channelId) { succeeded ->
                        if (succeeded) {
                            print("Alice added to channel withId = $channelId")
                            emitter.onNext(succeeded)
                        }
                    }

                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { succeeded ->
                print("All users have been added to channel")
            }
        disposables.add(disposable)
    }



    override fun onDestroy() {
        super.onDestroy()
        disposables.forEach {
            it.dispose()
        }
    }
}