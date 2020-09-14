package com.maliotis.muzmatch_excercise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.maliotis.muzmatch_excercise.controller.Operations
import com.maliotis.muzmatch_excercise.fragments.ChatFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class MainActivity : AppCompatActivity() {

    val TAG = MainActivity::class.java.simpleName

    // Realm Ids
    var loggedInUserId: String? = null
    var channelId: String? = null
    var bobIdSubject = PublishSubject.create<String>()
    var aliceIdSubject = PublishSubject.create<String>()
    var channelIdSubject = PublishSubject.create<String>()
    var usersChannelZipObservable: Observable<MutableList<String>>? = null

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

        channelIdSubject = PublishSubject.create<String>()
        aliceIdSubject = PublishSubject.create<String>()
        bobIdSubject = PublishSubject.create<String>()

        addUsersToChannelObserver()

        // Setting default user - Bob
        val disposableDefaultUser = Operations.createUser(defaultUser) { userId ->
            Log.d(TAG, "onCreate: Bob's id = $userId")
            loggedInUserId = userId
            bobIdSubject.onNext(userId)
        }

        // Creating Alice if doesn't exist
        val disposableAlice = Operations.createUser(alice) { userId ->
            Log.d(TAG, "onCreate: Alice's id = $userId")
            aliceIdSubject.onNext(userId)
        }

        // Setting the channel for communication
        val disposableChannel = Operations.createChannel(defaultChannelName) { cid ->
            channelId = cid
            channelIdSubject.onNext(cid)
        }

        disposables.add(disposableDefaultUser)
        disposables.add(disposableAlice)
        disposables.add(disposableChannel)

        // Init Chat fragment
        val chatFragment = ChatFragment()
        chatFragment.mainActivity = this
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, chatFragment)
            .commit()
    }

    /**
     * Observer for users (Bob, Alice) and channel
     * Once created/fetched proceed by adding them to the channel if they are not included.
     */
    private fun addUsersToChannelObserver() {
        usersChannelZipObservable = Observables.zip(bobIdSubject, aliceIdSubject, channelIdSubject) { bobId, aliceId, channelId ->
            mutableListOf(bobId, aliceId, channelId)
        }
        val disposable = (usersChannelZipObservable as Observable<MutableList<String>>)
            .observeOn(Schedulers.io())
            .flatMap { usersChannelId ->
                Observable.create<MutableList<String>> { emitter ->
                    // Add Bob to channel
                    val bobId = usersChannelId.first()
                    Log.d(TAG, "addUsersToChannelObserver: bobId = $bobId")
                    val channelId = usersChannelId[2]
                    Operations.addUserToChannel(bobId, channelId) { succeeded ->
                        if (succeeded) {
                            Log.d(TAG, "addUsersToChannelObserver: Bob added to channel withId = $channelId")
                            emitter.onNext(usersChannelId)
                        } else {
                            Log.d(TAG, "addUsersToChannelObserver: Bob wasn't added")
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
                            Log.d(TAG, "addUsersToChannelObserver: Alice added to channel withId = $channelId")
                            emitter.onNext(succeeded)
                        } else {
                            Log.d(TAG, "addUsersToChannelObserver: Alice wasn't added")
                        }
                    }

                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { succeeded ->
                if (succeeded)
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