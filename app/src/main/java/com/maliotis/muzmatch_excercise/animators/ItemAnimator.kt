package com.maliotis.muzmatch_excercise.animators


import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.content.Context
import android.util.Log
import android.view.animation.BounceInterpolator
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.recyclerview.widget.SimpleItemAnimator
import com.maliotis.muzmatch_excercise.R
import kotlinx.android.synthetic.main.chat_item_right_layout.view.*
import org.w3c.dom.Text


/**
 * Created by petrosmaliotis on 12/09/2020.
 */
class ItemAnimator(var context: Context, val onAnimationStart: (IntArray) -> Unit) : SimpleItemAnimator() {

    var triggerCallback = false

    override fun animateRemove(holder: ViewHolder?): Boolean {
        return false
    }

    /**
     * Trick to wait for recyclerView to add in the newly inserted item and
     * the correct location on screen
     */
    override fun animateAdd(holder: ViewHolder): Boolean {

        val view = holder.itemView
        val animation = ValueAnimator.ofFloat(-1f, 0f).apply {

            addListener(object: Animator.AnimatorListener {
                override fun onAnimationEnd(animation: Animator?) {
                    if (triggerCallback) {
                        triggerCallback = false
                        val intArray = IntArray(2)
                        view.getLocationInWindow(intArray)
                        this@ItemAnimator.onAnimationStart(intArray)
                    }
                }
                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationStart(animation: Animator?) {}
            })

            addUpdateListener { animation: ValueAnimator ->
                val value = animation.animatedValue as Float
                view.translationY = value
            }
        }
        animation.start()
        return true
    }

    override fun animateMove(
        holder: ViewHolder?,
        fromX: Int,
        fromY: Int,
        toX: Int,
        toY: Int
    ): Boolean {
        return false
    }

    override fun animateChange(
        oldHolder: ViewHolder?,
        newHolder: ViewHolder?,
        fromLeft: Int,
        fromTop: Int,
        toLeft: Int,
        toTop: Int
    ): Boolean {
        return false
    }

    override fun runPendingAnimations() {}
    override fun endAnimation(item: ViewHolder) {}
    override fun endAnimations() {}
    override fun isRunning(): Boolean {
        return false
    }


}