package com.chico.myhomebookkeeping

import com.chico.myhomebookkeeping.ui.calc.PaymentCalculatorEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PaymentCalculatorEngineTest {
    private val calculator = PaymentCalculatorEngine()

    @Test
    fun evaluatesBasicPaymentMath() {
        assertEquals(7.0, calculator.evaluate("1+2*3").getOrThrow(), 0.0)
        assertEquals(9.0, calculator.evaluate("(1+2)*3").getOrThrow(), 0.0)
        assertEquals(2.5, calculator.evaluate("10/4").getOrThrow(), 0.0)
    }

    @Test
    fun evaluatesPercentAsPartOfAmount() {
        assertEquals(0.1, calculator.evaluate("10%").getOrThrow(), 0.0)
        assertEquals(50.5, calculator.evaluate("50+50%").getOrThrow(), 0.0)
    }

    @Test
    fun rejectsInvalidExpressions() {
        assertTrue(calculator.evaluate("1/0").isFailure)
        assertTrue(calculator.evaluate("1+").isFailure)
        assertTrue(calculator.evaluate("(1+2").isFailure)
    }
}
