package com.maliotis.muzmatch_excercise.helpers

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue




/**
 * Created by petrosmaliotis on 13/09/2020.
 */

object ScreenSizeHelper {
    fun fromDPToPixel(dip: Float, context: Context): Int {
        val r: Resources = context.resources
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dip,
            r.displayMetrics
        )
        return px.toInt()
    }

    fun getNavBarHeight(context: Context): Int {
        val resources: Resources = context.resources
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else 0
    }

    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resourceId: Int = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }
}