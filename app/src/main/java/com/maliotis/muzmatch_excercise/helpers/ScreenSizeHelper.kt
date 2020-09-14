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

    fun getUsableToRealScreenDifferenceOnYAxis(context: Context): Int {
        return getNavBarHeight(context) + getStatusBarHeight(context) + fromDPToPixel(6f, context)
    }

    /**
     * return the middle point(middleX, middleY) of a line with point0(x0, y0) and point1(x1, y1)
     */
    fun findPointInLine(x0: Float, y0: Float, x1: Float, y1: Float, divider: Float): IntArray {
        val returnArray = IntArray(2)

        val middleX = (x0 + x1) / divider
        val middleY = (y0 + y1) / divider

        returnArray[0] = middleX.toInt()
        returnArray[1] = middleY.toInt()

        return returnArray
    }
}