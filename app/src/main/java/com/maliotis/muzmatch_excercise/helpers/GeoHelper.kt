package com.maliotis.muzmatch_excercise.helpers


import android.annotation.TargetApi
import android.graphics.Path
import android.os.Build
import android.transition.PathMotion
import kotlin.math.cos
import kotlin.math.sin


/**
 * Created by petrosmaliotis on 11/09/2020.
 */
object GeoHelper {

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