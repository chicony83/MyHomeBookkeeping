package com.chico.myhomebookkeeping.obj

import android.graphics.Color
import java.util.*

object GenerateColor {
    val rnd: Random = Random(System.currentTimeMillis())

    fun generateRandomColor(): Int {
        // This is the base color which will be mixed with the generated one
        val baseColor: Int = Color.GRAY
        val baseRed: Int = Color.red(baseColor)
        val baseGreen: Int = Color.green(baseColor)
        val baseBlue: Int = Color.blue(baseColor)
        val red: Int = (baseRed + rnd.nextInt(256)) / 2
        val green: Int = (baseGreen + rnd.nextInt(256)) / 2
        val blue: Int = (baseBlue + rnd.nextInt(256)) / 2
        return Color.rgb(red, green, blue)
    }
}