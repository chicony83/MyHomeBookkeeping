package com.chico.myhomebookkeeping.checks

class ModelCheck {
    fun isPositiveValue(value: Int): Boolean {
        return value > 0
    }
    fun isPositiveValue(value: Double): Boolean {
        return value > 0
    }
    fun isPositiveValue(value: Long): Boolean {
        return value > 0
    }
}