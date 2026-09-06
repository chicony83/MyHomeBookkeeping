package com.chico.myhomebookkeeping.domain

/** Transliteration to the traditional Belarusian Latin alphabet (Łacinka). */
object BelarusianLatin {
    private val sequenceMap = linkedMapOf(
        "дзь" to "dź",
        "дж" to "dž",
        "дз" to "dz",
        "зь" to "ź",
        "сь" to "ś",
        "ць" to "ć",
        "нь" to "ń",
        "ль" to "l"
    )

    private val simpleMap = mapOf(
        'а' to "a", 'б' to "b", 'в' to "v", 'г' to "h", 'ґ' to "g",
        'д' to "d", 'ж' to "ž", 'з' to "z", 'і' to "i", 'й' to "j",
        'к' to "k", 'м' to "m", 'н' to "n", 'о' to "o", 'п' to "p",
        'р' to "r", 'с' to "s", 'т' to "t", 'у' to "u", 'ў' to "ŭ",
        'ф' to "f", 'х' to "ch", 'ц' to "c", 'ч' to "č", 'ш' to "š",
        'ы' to "y", 'э' to "e", 'щ' to "šč", 'ъ' to "’", 'ь' to "’"
    )

    private val iotatedVowels = mapOf(
        'е' to ("je" to "ie"),
        'ё' to ("jo" to "io"),
        'ю' to ("ju" to "iu"),
        'я' to ("ja" to "ia")
    )

    private val vowelsAndSeparators = "аеёіоуыэюяАЕЁІОУЫЭЮЯўЎйЙьЬъЪ'’"
    private val softVowels = "еёіюяЕЁІЮЯ"

    fun transliterate(text: String): String {
        val result = StringBuilder(text.length)
        var index = 0
        while (index < text.length) {
            val sequence = sequenceMap.keys.firstOrNull { key ->
                text.regionMatches(index, key, 0, key.length, ignoreCase = true)
            }
            if (sequence != null) {
                val source = text.substring(index, index + sequence.length)
                result.append(matchCase(source, sequenceMap.getValue(sequence)))
                index += sequence.length
                continue
            }

            val source = text[index]
            val lower = source.lowercaseChar()
            val replacement = when {
                lower == 'л' -> {
                    val next = text.getOrNull(index + 1)
                    if (next != null && next in softVowels) "l" else "ł"
                }
                lower in iotatedVowels -> {
                    val previous = text.getOrNull(index - 1)
                    val (withJ, afterConsonant) = iotatedVowels.getValue(lower)
                    if (previous == null || !previous.isLetter() || previous in vowelsAndSeparators) {
                        withJ
                    } else {
                        afterConsonant
                    }
                }
                else -> simpleMap[lower]
            }
            result.append(replacement?.let { matchCase(source.toString(), it) } ?: source)
            index++
        }
        return result.toString()
    }

    private fun matchCase(source: String, replacement: String): String {
        return when {
            source.length > 1 && source.all(Char::isUpperCase) -> replacement.uppercase()
            source.first().isUpperCase() -> replacement.replaceFirstChar(Char::uppercaseChar)
            else -> replacement
        }
    }
}
