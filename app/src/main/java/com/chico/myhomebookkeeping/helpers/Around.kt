package com.chico.myhomebookkeeping.helpers

import java.math.BigDecimal
import java.math.RoundingMode

object Around {
    fun double(text: String): Double {
        return BigDecimal(text.replace(",","."))
            .setScale(2, RoundingMode.HALF_EVEN)
            .toDouble()
    }

}