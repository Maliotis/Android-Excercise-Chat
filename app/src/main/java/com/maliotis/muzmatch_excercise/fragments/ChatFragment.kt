package com.maliotis.muzmatch_excercise.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.maliotis.muzmatch_excercise.MainActivity
import com.maliotis.muzmatch_excercise.R
import com.maliotis.muzmatch_excercise.controller.Conversation
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by petrosmaliotis on 11/09/2020.
 */
class ChatFragment: Fragment() {

    // MainActivity reference
    lateinit var mainActivity: MainActivity

    // Views
    lateinit var textEntryEditText: EditText
    lateinit var stickerButton: ImageButton
    lateinit var sendButton: ImageButton

    // Observables
    lateinit var sendButtonObservable: Observable<String>
    lateinit var stickerButtonObservable: Observable<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val baseChat = inflater.inflate(R.layout.base_chat_layout, container, false)
        textEntryEditText = baseChat.findViewById(R.id.textEntryEditText)
        stickerButton = baseChat.findViewById(R.id.textEntryStickerButton)
        sendButton = baseChat.findViewById(R.id.textEntrySendButton)

        sendButtonObservable = createSendButtonObservable()
        stickerButtonObservable = createStickerButtonObservable()

        return baseChat
    }

    /**
     * Create the [sendButton] observable listener and
     * omits the text from the [textEntryEditText]
     */
    private fun createSendButtonObservable(): Observable<String> {
        return Observable.create<String> { emitter ->
            sendButton.setOnClickListener {
                val text = textEntryEditText.text.toString()
                emitter.onNext(text)
            }

            emitter.setCancellable {
                sendButton.setOnClickListener(null)
            }
        }
    }

    /**
     * Create the [stickerButton] observable listener and
     * omits the base64 text of the image selected
     *
     * Will be implemented as future feature
     */
    private fun createStickerButtonObservable(): Observable<String> {
        return Observable.create<String> {
            stickerButton.setOnClickListener {

            }

        }
    }

    override fun onStart() {
        super.onStart()

        val disposable = sendButtonObservable.subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(Schedulers.io())
            .flatMap { text ->
                Observable.create<String> { emitter ->
                    Conversation.sendMessageFrom(text, mainActivity.loggedInUserId, mainActivity.channelId)
                }
            }
    }
}