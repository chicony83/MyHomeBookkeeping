package com.chico.myhomebookkeeping.ui.calc

import kotlin.math.abs

/**
 * Small payment-focused calculator.
 *
 * It intentionally supports only the operations that are useful while entering money:
 * addition, subtraction, multiplication, division, percent and parentheses.
 */
class PaymentCalculatorEngine {
    fun evaluate(expression: String): Result<Double> {
        return runCatching {
            val parser = Parser(expression.filterNot(Char::isWhitespace))
            val value = parser.parseExpression()
            parser.requireEnd()
            require(value.isFinite()) { "Result is not finite" }
            if (abs(value) < ZERO_EPSILON) 0.0 else value
        }
    }

    private class Parser(private val expression: String) {
        private var position = 0

        fun parseExpression(): Double {
            var value = parseTerm()
            while (true) {
                value = when {
                    eat('+') -> value + parseTerm()
                    eat('-') -> value - parseTerm()
                    else -> return value
                }
            }
        }

        private fun parseTerm(): Double {
            var value = parsePercent()
            while (true) {
                value = when {
                    eat('*') -> value * parsePercent()
                    eat('/') -> {
                        val denominator = parsePercent()
                        require(abs(denominator) > ZERO_EPSILON) { "Division by zero" }
                        value / denominator
                    }
                    else -> return value
                }
            }
        }

        private fun parsePercent(): Double {
            var value = parseFactor()
            while (eat('%')) {
                value /= 100.0
            }
            return value
        }

        private fun parseFactor(): Double {
            if (eat('+')) return parseFactor()
            if (eat('-')) return -parseFactor()

            val value = when {
                eat('(') -> {
                    val nested = parseExpression()
                    require(eat(')')) { "Missing closing parenthesis" }
                    nested
                }
                peek()?.isDigit() == true || peek() == '.' -> parseNumber()
                else -> error("Unexpected character")
            }

            return value
        }

        private fun parseNumber(): Double {
            val start = position
            while (peek()?.isDigit() == true) position++
            if (eat('.')) {
                while (peek()?.isDigit() == true) position++
            }
            val token = expression.substring(start, position)
            require(token != "." && token.isNotEmpty()) { "Invalid number" }
            return token.toDouble()
        }

        fun requireEnd() {
            require(position == expression.length) { "Unexpected tail" }
        }

        private fun eat(char: Char): Boolean {
            if (peek() != char) return false
            position++
            return true
        }

        private fun peek(): Char? = expression.getOrNull(position)
    }

    companion object {
        private const val ZERO_EPSILON = 0.0000000001
    }
}
