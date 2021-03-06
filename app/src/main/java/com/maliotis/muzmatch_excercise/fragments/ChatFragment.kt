package com.maliotis.muzmatch_excercise.fragments

import android.animation.*
import android.content.Context
import android.graphics.Color
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Bundle
import android.text.InputType
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import com.maliotis.muzmatch_excercise.MainActivity
import com.maliotis.muzmatch_excercise.R
import com.maliotis.muzmatch_excercise.adapters.MessageListAdapter
import com.maliotis.muzmatch_excercise.animators.ItemAnimator
import com.maliotis.muzmatch_excercise.controller.Operations
import com.maliotis.muzmatch_excercise.data.realmObjects.Channel
import com.maliotis.muzmatch_excercise.data.realmObjects.Message
import com.maliotis.muzmatch_excercise.data.realmObjects.User
import com.maliotis.muzmatch_excercise.helpers.DateHelper
import com.maliotis.muzmatch_excercise.helpers.ScreenSizeHelper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


/**
 * Created by petrosmaliotis on 11/09/2020.
 */
class ChatFragment: Fragment() {


    val TAG = ChatFragment::class.java.simpleName

    // MainActivity reference
    lateinit var mainActivity: MainActivity

    // Views
    lateinit var baseChat: ConstraintLayout
    lateinit var textEntryEditText: EditText
    lateinit var stickerButton: ImageButton
    lateinit var sendButton: ImageButton
    lateinit var aliceButton: Button
    lateinit var bobButton: Button
    lateinit var recyclerView: RecyclerView

    // Callbacks
    private val onItemAnimationStartCallback = { intArray: IntArray ->

        val layoutView = recyclerView.layoutManager?.findViewByPosition(dataForAdapter.size - 1) as? RelativeLayout
        val rightTextView = layoutView?.findViewById<TextView>(R.id.rightTextView)
        layoutView!!.getLocationInWindow(intArray)
        // Use relatives layout y - and readjust from true screen size to usable screen size on axis y
        intArray[1] = intArray[1] - ScreenSizeHelper.getUsableToRealScreenDifferenceOnYAxis(mainActivity)

        val rightTextViewArray = IntArray(2)
        rightTextView!!.getLocationInWindow(rightTextViewArray)
        // Use rightTextViews x
        intArray[0] = rightTextViewArray[0]
        createTextViewToAnimate(intArray)
        textEntryEditText.text.clear()

    }

    // Animators
    lateinit var simpleItemAnimator: ItemAnimator

    // Adapters
    lateinit var messageListAdapter: MessageListAdapter

    // SmoothScroller
    var smoothScroller: SmoothScroller? = null

    // Data
    var dataForAdapter: MutableList<Any> = mutableListOf()
    var channel: Channel? = null

    // Observables
    lateinit var sendButtonObservable: Observable<String>
    lateinit var stickerButtonObservable: Observable<String>
    lateinit var aliceButtonObservable: Observable<String>
    lateinit var bobButtonObservable: Observable<String>

    // Disposable
    private val disposables = mutableListOf<Disposable>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (mainActivity.channelId != null && mainActivity.loggedInUserId != null) {
            populateData(mainActivity.channelId!!)
            messageListAdapter = MessageListAdapter(dataForAdapter, mainActivity.loggedInUserId!!, mainActivity)
        }


        baseChat = inflater.inflate(R.layout.base_chat_layout, container, false) as ConstraintLayout
        textEntryEditText = baseChat.findViewById(R.id.textEntryEditText)
        stickerButton = baseChat.findViewById(R.id.textEntryStickerButton)
        sendButton = baseChat.findViewById(R.id.textEntrySendButton)
        aliceButton = baseChat.findViewById(R.id.aliceUseButton)
        bobButton = baseChat.findViewById(R.id.bobUserButton)
        recyclerView = baseChat.findViewById<RecyclerView>(R.id.messageListRecyclerView).apply {
            layoutManager = LinearLayoutManager(mainActivity)
            if (::messageListAdapter.isInitialized) {
                adapter = messageListAdapter
            }
            simpleItemAnimator = ItemAnimator(mainActivity, onItemAnimationStartCallback)
            itemAnimator = simpleItemAnimator
        }

        sendButtonObservable = createSendButtonObservable()
        aliceButtonObservable = createAliceButtonObservable()
        bobButtonObservable = createBobButtonObservable()
        stickerButtonObservable = createStickerButtonObservable()

        textEntryEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES

        return baseChat
    }

    private fun initSmoothScroller() {
        smoothScroller = object : LinearSmoothScroller(recyclerView.context) {
            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return 50f / displayMetrics.densityDpi
            }
        }
    }

    /**
     * Create the [sendButton] observable listener and
     * omits the text from the [textEntryEditText]
     */
    private fun createSendButtonObservable(): Observable<String> {
        return Observable.create<String> { emitter ->
            sendButton.setOnClickListener {
                if (textEntryEditText.text.toString() != "") {
                    val text = textEntryEditText.text.toString()
                    sendButton.isEnabled = false
                    emitter.onNext(text)
                }

            }

            emitter.setCancellable {
                sendButton.setOnClickListener(null)
            }
        }
    }

    /**
     * Create the [aliceButton] observable listener and
     * changes the loggedInUser
     */
    private fun createAliceButtonObservable(): Observable<String> {
        return Observable.create<String> { emitter ->
            aliceButton.setOnClickListener {
                val name =  "Alice"
                emitter.onNext(name)
            }

            emitter.setCancellable {
                aliceButton.setOnClickListener(null)
            }
        }
    }

    /**
     * Create the [bobButton] observable listener and
     * changes the loggedInUser
     */
    private fun createBobButtonObservable(): Observable<String> {
        return Observable.create<String> { emitter ->
            bobButton.setOnClickListener {
                val name =  "Bob"
                emitter.onNext(name)
            }

            emitter.setCancellable {
                bobButton.setOnClickListener(null)
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

    /**
     * Populate [dataForAdapter] with messages and date strings
     */
    private fun populateData(channelId: String) {
        // Get all messages in UI view to access fields

        val allMessages = Operations.getAllMessagesForChannelOnCurrentThread(channelId)
        // Get channel
        channel = Operations.getChannelOnCurrentThread(channelId)
        allMessages?.forEachIndexed { i, msg ->
            if (i == 0) {
                // Display Divider for first message
                val stringDate = DateHelper.getStringFormattedDate(msg.time)
                dataForAdapter.add(stringDate)
                dataForAdapter.add(msg)

            } else {
                // check previous message if there is more than one hour difference
                val prevMsg = allMessages[i-1]
                val timeDiff = DateHelper.getTimeDifferenceInHours(prevMsg.time, msg.time)
                if (timeDiff >= 1) {
                    val stringDate = DateHelper.getStringFormattedDate(msg.time)
                    dataForAdapter.add(stringDate)
                    dataForAdapter.add(msg)
                } else {
                    dataForAdapter.add(msg)
                }
            }
        }

        // If there are no messages display time
        if (allMessages.isNullOrEmpty()) {
            val time = System.currentTimeMillis()
            val stringDate = DateHelper.getStringFormattedDate(time)
            dataForAdapter.add(stringDate)
        }
    }

    /**
     * Add recent message in [dataForAdapter] and notify that a new item inserted
     */
    private fun addRecentMessage(message: Message?) {
        if (message == null) {
            Log.d(TAG, "addRecentMessage: message was null")
            return
        }
        val last = dataForAdapter.last()
        if (last is Message) {
            // compare messages and add in dataForAdapter and messageListAdapter.items
            checkPreviousMessageForTimeDiffAndAdd(last, message)
            checkPreviousMessageForBubbleTail(last, message)
        } else if (last is String) {
            dataForAdapter.add(message)
        }

        recyclerView.scheduleLayoutAnimation()
        messageListAdapter.notifyItemInserted(dataForAdapter.size - 1)
        smoothScroller?.targetPosition = dataForAdapter.size - 1
        recyclerView.layoutManager?.startSmoothScroll(smoothScroller)
        simpleItemAnimator.triggerCallback = true
    }

    /**
     * A message has a tail when any of the following 3 criteria are met:
     * It is the most recent message in the conversation
     * The message after it is sent by the other user
     * The message after it was sent more than 20 seconds afterwards
     *
     * If none of these criteria are met we remove the bubble
     */
    private fun checkPreviousMessageForBubbleTail(last: Message, message: Message) {
        val timeDiff = DateHelper.getTimeDifferenceInSeconds(last.time, message.time)
        if (last.user?.name == message.user?.name && timeDiff < 20) {
            // If the previous message is from the same user remove bubble
            if (recyclerView.layoutManager?.findViewByPosition(dataForAdapter.size - 2) is RelativeLayout) {
                val rightTextView = (recyclerView.layoutManager?.findViewByPosition(dataForAdapter.size - 2) as RelativeLayout).findViewById<TextView>(R.id.rightTextView)
                val drawable = ResourcesCompat.getDrawable(resources, R.drawable.bubble_chat_right, null)
                rightTextView.background = drawable
                val padding = ScreenSizeHelper.fromDPToPixel(8f, mainActivity)
                rightTextView.setPadding(padding,padding,padding,padding)

                val animator = ValueAnimator.ofInt((rightTextView?.layoutParams as RelativeLayout.LayoutParams).bottomMargin, 0)
                animator.duration = 300
                animator.addUpdateListener {
                    val value = it.animatedValue as Int
                    val params = (rightTextView.layoutParams as RelativeLayout.LayoutParams)
                    params.bottomMargin = value
                    rightTextView.layoutParams = params
                }
                animator.start()
                Operations.changeTailOnMessageOnCurrentThread(last.id!!, false)
            }
        }
    }

    /**
     * Helper method that check the time difference is >= 1hour and adds in [dataForAdapter]
     */
    private fun checkPreviousMessageForTimeDiffAndAdd(prevMsg: Message, msg: Message) {
        val timeDiff = DateHelper.getTimeDifferenceInHours(prevMsg.time, msg.time)
        if (timeDiff > 1) {
            val stringDate = DateHelper.getStringFormattedDate(msg.time)
            dataForAdapter.add(stringDate)
            dataForAdapter.add(msg)
        } else {
            dataForAdapter.add(msg)
        }
    }

    override fun onStart() {
        super.onStart()

        initSmoothScroller()

        // Send Button subscriber
        val disposable = sendButtonObservable.subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(Schedulers.io())
            .flatMap { text ->
                Observable.create<String> { emitter ->
                    val messageId = Operations.sendMessageFrom(text, mainActivity.loggedInUserId!!, mainActivity.channelId!!)
                    emitter.onNext(messageId ?: "")
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { messageId ->
                if (messageId.isNotEmpty()) {
                    val message = Operations.getMessageOnCurrentThread(messageId)
                    addRecentMessage(message)
                }
            }

        // Alice Button subscriber
        val aliceDisposable = aliceButtonObservable.subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(Schedulers.io())
            .flatMap { text ->
                Observable.create<String> { emitter ->
                    Operations.loginWithUser(text) { loggedInUserId ->
                        mainActivity.loggedInUserId = loggedInUserId
                        emitter.onNext(loggedInUserId)
                    }
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { userId ->
                Observable.create<User> { emitter ->
                    val user = Operations.getUserOnCurrentThread(userId)
                    emitter.onNext(user!!)
                }
            }
            .subscribe { loggedInUser ->
                dataForAdapter.clear()
                populateData(loggedInUser.channels.first()?.id!!)
                messageListAdapter = MessageListAdapter(dataForAdapter, loggedInUser.id!!, mainActivity)
                recyclerView.adapter = messageListAdapter
                recyclerView.scrollToPosition(dataForAdapter.size - 1)

            }

        // Bob button subscriber
        val bobDisposable = bobButtonObservable.subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(Schedulers.io())
            .flatMap { text ->
                Observable.create<String> { emitter ->
                    Operations.loginWithUser(text) { loggedInUserId ->
                        mainActivity.loggedInUserId = loggedInUserId
                        emitter.onNext(loggedInUserId)
                    }
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { userId ->
                Observable.create<User> { emitter ->
                    val user = Operations.getUserOnCurrentThread(userId)
                    emitter.onNext(user!!)
                }
            }
            .subscribe { loggedInUser ->
                dataForAdapter.clear()
                populateData(loggedInUser.channels.first()?.id!!)
                messageListAdapter = MessageListAdapter(dataForAdapter, loggedInUser.id!!, mainActivity)
                recyclerView.adapter = messageListAdapter
                recyclerView.scrollToPosition(dataForAdapter.size - 1)

            }

        // When all users and channel have been initialized/fetched populate the recycler view
        val zipDisposable = mainActivity.usersChannelZipObservable!!.subscribe {
            val channelId = it.last()
            val bobsId = it.first()
            populateData(channelId)
            messageListAdapter = MessageListAdapter(dataForAdapter, bobsId, mainActivity)
            recyclerView.adapter = messageListAdapter
            recyclerView.scrollToPosition(dataForAdapter.size - 1)
        }

        // Add disposables to dispose later
        disposables.add(disposable)
        disposables.add(aliceDisposable)
        disposables.add(bobDisposable)
        disposables.add(zipDisposable)
    }

    /**
     * Creates the text view that will animate into the recycler view with an arc motion
     */
    private fun createTextViewToAnimate(endCoordIntArray: IntArray) {
        val coordArray = IntArray(2)

        val textViewToAnimate = TextView(mainActivity).apply {
            // Set width and height
            val width = textEntryEditText.width
            val height = textEntryEditText.height
            layoutParams = ViewGroup.LayoutParams(width, height)

            text = (dataForAdapter.last() as Message).content?.text
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTextColor(Color.WHITE)
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            gravity = Gravity.CENTER

            // Set coordinates
            textEntryEditText.getLocationInWindow(coordArray) // getLocationOnWindow return x,y on the real screen size
            coordArray[1] = coordArray[1] - ScreenSizeHelper.getUsableToRealScreenDifferenceOnYAxis(mainActivity)
            x = coordArray[0].toFloat()
            y = coordArray[1].toFloat()

            // Add background
            val layerDrawable = ResourcesCompat.getDrawable(resources, R.drawable.bubble_with_tail_right_white, null) as LayerDrawable
            background = layerDrawable
        }
        baseChat.addView(textViewToAnimate)
        textViewToAnimate.bringToFront()
        ViewCompat.setTranslationZ(textViewToAnimate, 1f)
        textViewToAnimate.requestFocus()

        val path = getPathWithArc(endCoordIntArray, coordArray)

        // Path Animator
        val pathAnimator = ObjectAnimator.ofFloat(textViewToAnimate, View.X, View.Y, path)

        val params = textViewToAnimate.layoutParams
        val rightTextView = (recyclerView.layoutManager?.findViewByPosition(dataForAdapter.size - 1) as RelativeLayout).findViewById<TextView>(R.id.rightTextView)

        // Width Animator
        val widthAnimator = ValueAnimator.ofInt(textEntryEditText.width, rightTextView!!.width)
        widthAnimator.addUpdateListener {
            val value = it.animatedValue as Int
            params.width = value
            textViewToAnimate.layoutParams = params
        }

        // Height Animator
        val heightAnimator = ValueAnimator.ofInt(textEntryEditText.height, rightTextView.height)
        heightAnimator.addUpdateListener {
            val value = it.animatedValue as Int
            params.height = value
            textViewToAnimate.layoutParams = params
        }

        // Color Animator
        val colorAnimator = ValueAnimator.ofObject(ArgbEvaluator(), Color.WHITE, getThemePrimaryColor(mainActivity))
        colorAnimator.addUpdateListener {
            val value = it.animatedValue as Int
            val layerDrawable = textViewToAnimate.background as LayerDrawable
            val gradientDrawable = layerDrawable.findDrawableByLayerId(R.id.bubble_white_main) as GradientDrawable
            val tailDrawable = layerDrawable.findDrawableByLayerId(R.id.bubble_white_main_tail) as VectorDrawable

            gradientDrawable.colors = intArrayOf(value, value)
            tailDrawable.setColorFilter(value, PorterDuff.Mode.SRC_ATOP)

            layerDrawable.setDrawableByLayerId(R.id.bubble_white_main, gradientDrawable)
            layerDrawable.setDrawableByLayerId(R.id.bubble_white_main_tail, tailDrawable)

            textViewToAnimate.background = layerDrawable
        }

        // Path Animator Listener - OnEnd remove the view to reveal the actual item in the recycler view
        pathAnimator.addListener(object: Animator.AnimatorListener {
            override fun onAnimationEnd(animation: Animator?) {
                rightTextView.alpha = 1f
                Operations.changeAlphaOnMessageOnCurrentThread((dataForAdapter.last() as Message).id!!, 1f)
                baseChat.removeView(textViewToAnimate)
                textEntryEditText.isEnabled = true
                sendButton.isEnabled = true
            }
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {}
            override fun onAnimationRepeat(animation: Animator?) {}
        })

        // Play all animations together
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(pathAnimator, widthAnimator, heightAnimator, colorAnimator)
        animatorSet.start()
    }

    private fun getPathWithArc(endCoordIntArray: IntArray, coordArray: IntArray): Path {
        return Path().apply {
            val displayMetrics = DisplayMetrics()
            mainActivity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            val height = displayMetrics.heightPixels
            val width = displayMetrics.widthPixels

            val xy3Array = endCoordIntArray

            val endOfScreenX = width
            val endOfScreenY = coordArray[1].toInt() + textEntryEditText.height

            val x0 = coordArray[0].toFloat()
            val y0 = coordArray[1].toFloat()
            val x3 = xy3Array[0].toFloat()
            val y3 = xy3Array[1].toFloat()

            moveTo(x0, y0)

            // x1, y1 curve from first point - calculate by finding the line from x0,y0 to the bottom|right of the screen and divide by 2.2
            val xy1Array = ScreenSizeHelper.findPointInLine(
                x0,
                y0,
                endOfScreenX.toFloat(),
                endOfScreenY.toFloat(),
                2.2f
            )
            val x1 = xy1Array[0]
            val y1 = xy1Array[1]
            // x2, y2 curve from last point - calculate by finding the line from  x4,y4 to the bottom|right of the screen and divide by 2.1
            val xy2Array = ScreenSizeHelper.findPointInLine(
                x3,
                y3,
                endOfScreenX.toFloat(),
                endOfScreenY.toFloat(),
                2.1f
            )
            val x2 = xy2Array[0]
            val y2 = xy2Array[1]

            cubicTo(x1.toFloat(), y1.toFloat(), x2.toFloat(), y2.toFloat(), x3, y3)

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.forEach { disposable ->
            disposable.dispose()
        }
    }

    private fun getThemePrimaryColor(context: Context): Int {
        val colorAttr: Int = android.R.attr.colorPrimary
        val outValue = TypedValue()
        context.theme.resolveAttribute(colorAttr, outValue, true)
        return outValue.data
    }
}