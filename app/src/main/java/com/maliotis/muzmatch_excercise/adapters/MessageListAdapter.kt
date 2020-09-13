package com.maliotis.muzmatch_excercise.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.maliotis.muzmatch_excercise.R
import com.maliotis.muzmatch_excercise.data.realmObjects.Message
import com.maliotis.muzmatch_excercise.helpers.ScreenSizeHelper

/**
 * Created by petrosmaliotis on 11/09/2020.
 */
class MessageListAdapter(var items: MutableList<Any>, val loggedInUserId: String, val context: Context): RecyclerView.Adapter<MessageListAdapter.MLViewHolder>() {

    val TAG = MessageListAdapter::class.java.simpleName

    val TYPE_RIGHT = 0
    val TYPE_LEFT = 1
    val TYPE_DIVIDER = 2

    class MLViewHolder(val layout: ViewGroup, val leftTextView: TextView?, val rightTextView: TextView?): RecyclerView.ViewHolder(layout)

    override fun getItemViewType(position: Int): Int {
        return if (items[position] is Message) {
            if ((items[position] as Message).user?.id == loggedInUserId) {
                TYPE_RIGHT
            } else {
                TYPE_LEFT
            }
        } else {
            // Divider
            TYPE_DIVIDER
        }
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MLViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        if (viewType == TYPE_RIGHT) {
            val rootView = inflater
                .inflate(R.layout.chat_item_right_layout, parent, false) as RelativeLayout
            val rightTextView = rootView.findViewById<TextView>(R.id.rightTextView)
            return MLViewHolder(rootView, null, rightTextView)
        } else if (viewType == TYPE_LEFT){
            val rootView = inflater
                .inflate(R.layout.chat_item_left_layout, parent, false) as RelativeLayout
            val leftTextView = rootView.findViewById<TextView>(R.id.leftTextView)
            return MLViewHolder(rootView, leftTextView, null)
        } else {
            // Divider
            val rootView = inflater
                .inflate(R.layout.chat_item_divider_date, parent, false) as LinearLayout
            return MLViewHolder(rootView, null, null)
        }
    }

    override fun onBindViewHolder(holder: MLViewHolder, position: Int) {
        // TODO: Change data and margin_bottom if the view doesn't have bubble_tail
        val layout = holder.layout

        if (layout is RelativeLayout) {
            // Message
            val rightTextView = holder.rightTextView
            val leftTextView = holder.leftTextView

            if (rightTextView != null) {
                rightTextView.apply {
                    text = (items[position] as Message).content?.text ?: ""
                }
                val message = items[position] as Message
                rightTextView.alpha = message.alpha!!
                Log.d(TAG, "onBindViewHolder: chatTextView.tail = ${message.tail}")
                if (message.tail != true) {
                    val drawable = ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.bubble_chat_right,
                        null
                    )
                    rightTextView.background = drawable
                    val padding = ScreenSizeHelper.fromDPToPixel(8f, context)
                    rightTextView.setPadding(padding, padding, padding, padding)
                    (rightTextView.layoutParams as RelativeLayout.LayoutParams).bottomMargin = 0
                } else {
                    val drawable = ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.bubble_with_tail_right,
                        null
                    )
                    rightTextView.background = drawable
                    val padding = ScreenSizeHelper.fromDPToPixel(8f, context)
                    rightTextView.setPadding(padding, padding, padding, padding)
                    val margin = ScreenSizeHelper.fromDPToPixel(14f, context)
                    (rightTextView.layoutParams as RelativeLayout.LayoutParams).bottomMargin =
                        margin
                }
            } else if (leftTextView != null) {
                leftTextView.apply {
                    text = (items[position] as Message).content?.text ?: ""
                }

                val chatPersonImage = (leftTextView.parent as RelativeLayout).findViewById<ImageView>(R.id.chatPersonImage)

                val message = items[position] as Message
                if (message.user?.name == "Alice") {
                    chatPersonImage.setImageResource(R.drawable.ic_alice_24px)
                } else {
                    chatPersonImage.setImageResource(R.drawable.ic_person_24px)
                }
                leftTextView.alpha = message.alpha!!
                Log.d(TAG, "onBindViewHolder: chatTextView.tail = ${message.tail}")
                if (message.tail != true) {
                    val drawable = ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.bubble_chat_left,
                        null
                    )
                    leftTextView.background = drawable
                    val padding = ScreenSizeHelper.fromDPToPixel(8f, context)
                    leftTextView.setPadding(padding, padding, padding, padding)
                    (leftTextView.layoutParams as RelativeLayout.LayoutParams).bottomMargin = 0
                } else {
                    val drawable = ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.bubble_with_tail_left,
                        null
                    )
                    leftTextView.background = drawable
                    val padding = ScreenSizeHelper.fromDPToPixel(8f, context)
                    leftTextView.setPadding(padding, padding, padding, padding)
                    val margin = ScreenSizeHelper.fromDPToPixel(14f, context)
                    (leftTextView.layoutParams as RelativeLayout.LayoutParams).bottomMargin =
                        margin
                }
            }

        } else if (layout is LinearLayout) {
            // Divider

            val dataItem = items[position] as String
            Log.d(TAG, "onBindViewHolder: dataItem = $dataItem")
            val dayTextView = layout.findViewById<TextView>(R.id.chatItemDay)
            val hourTextView = layout.findViewById<TextView>(R.id.chatItemTime)

            val dataItems = dataItem.split(" ")
            val day = dataItems[0]
            val hour = dataItems[3]

            dayTextView.text = day
            hourTextView.text = hour
        }
    }
}