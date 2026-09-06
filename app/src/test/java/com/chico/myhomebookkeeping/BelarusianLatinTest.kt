package com.chico.myhomebookkeeping

import com.chico.myhomebookkeeping.domain.BelarusianLatin
import org.junit.Assert.assertEquals
import org.junit.Test

class BelarusianLatinTest {
    @Test
    fun transliteratesTraditionalBelarusianLatinLetters() {
        assertEquals("Biełaruskaja mova", BelarusianLatin.transliterate("Беларуская мова"))
        assertEquals("Nałady", BelarusianLatin.transliterate("Налады"))
        assertEquals(
            "Zmianić paradak katehoryj",
            BelarusianLatin.transliterate("Змяніць парадак катэгорый")
        )
        assertEquals("Najaŭnyja hrošy", BelarusianLatin.transliterate("Наяўныя грошы"))
        assertEquals("Dzieci", BelarusianLatin.transliterate("Дзеці"))
    }
}
