package com.chico.myhomebookkeeping.helpers

import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.entity.FastPayments
import com.chico.myhomebookkeeping.db.entity.ParentCategories
import com.chico.myhomebookkeeping.obj.Constants

// English names are canonical in DB rows; Russian columns are optional localized display values.
fun CashAccount.displayName(languageTag: String): String =
    accountNameBeLatn.takeIf { languageTag == Constants.APP_LANGUAGE_BELARUSIAN_LATIN && !it.isNullOrBlank() }
        ?: accountNameBe.takeIf { languageTag == Constants.APP_LANGUAGE_BELARUSIAN && !it.isNullOrBlank() }
        ?: accountNameDe.takeIf { languageTag == Constants.APP_LANGUAGE_GERMAN && !it.isNullOrBlank() }
        ?: accountNamePl.takeIf { languageTag == Constants.APP_LANGUAGE_POLISH && !it.isNullOrBlank() }
        ?: accountNameRu.takeIf { languageTag == Constants.APP_LANGUAGE_RUSSIAN && !it.isNullOrBlank() }
        ?: accountName

fun Categories.displayName(languageTag: String): String =
    categoryNameBeLatn.takeIf { languageTag == Constants.APP_LANGUAGE_BELARUSIAN_LATIN && !it.isNullOrBlank() }
        ?: categoryNameBe.takeIf { languageTag == Constants.APP_LANGUAGE_BELARUSIAN && !it.isNullOrBlank() }
        ?: categoryNameDe.takeIf { languageTag == Constants.APP_LANGUAGE_GERMAN && !it.isNullOrBlank() }
        ?: categoryNamePl.takeIf { languageTag == Constants.APP_LANGUAGE_POLISH && !it.isNullOrBlank() }
        ?: categoryNameRu.takeIf { languageTag == Constants.APP_LANGUAGE_RUSSIAN && !it.isNullOrBlank() }
        ?: categoryName

fun ParentCategories.displayName(languageTag: String): String =
    nameBeLatn.takeIf { languageTag == Constants.APP_LANGUAGE_BELARUSIAN_LATIN && !it.isNullOrBlank() }
        ?: nameBe.takeIf { languageTag == Constants.APP_LANGUAGE_BELARUSIAN && !it.isNullOrBlank() }
        ?: nameDe.takeIf { languageTag == Constants.APP_LANGUAGE_GERMAN && !it.isNullOrBlank() }
        ?: namePl.takeIf { languageTag == Constants.APP_LANGUAGE_POLISH && !it.isNullOrBlank() }
        ?: nameRu.takeIf { languageTag == Constants.APP_LANGUAGE_RUSSIAN && !it.isNullOrBlank() }
        ?: name

fun FastPayments.displayName(languageTag: String): String =
    nameFastPaymentBeLatn.takeIf { languageTag == Constants.APP_LANGUAGE_BELARUSIAN_LATIN && !it.isNullOrBlank() }
        ?: nameFastPaymentBe.takeIf { languageTag == Constants.APP_LANGUAGE_BELARUSIAN && !it.isNullOrBlank() }
        ?: nameFastPaymentDe.takeIf { languageTag == Constants.APP_LANGUAGE_GERMAN && !it.isNullOrBlank() }
        ?: nameFastPaymentPl.takeIf { languageTag == Constants.APP_LANGUAGE_POLISH && !it.isNullOrBlank() }
        ?: nameFastPaymentRu.takeIf { languageTag == Constants.APP_LANGUAGE_RUSSIAN && !it.isNullOrBlank() }
        ?: nameFastPayment

fun localizedName(
    name: String?,
    nameRu: String?,
    languageTag: String,
    namePl: String? = null,
    nameDe: String? = null,
    nameBe: String? = null,
    nameBeLatn: String? = null
): String? =
    nameBeLatn.takeIf { languageTag == Constants.APP_LANGUAGE_BELARUSIAN_LATIN && !it.isNullOrBlank() }
        ?: nameBe.takeIf { languageTag == Constants.APP_LANGUAGE_BELARUSIAN && !it.isNullOrBlank() }
        ?: nameDe.takeIf { languageTag == Constants.APP_LANGUAGE_GERMAN && !it.isNullOrBlank() }
        ?: namePl.takeIf { languageTag == Constants.APP_LANGUAGE_POLISH && !it.isNullOrBlank() }
        ?: nameRu.takeIf { languageTag == Constants.APP_LANGUAGE_RUSSIAN && !it.isNullOrBlank() }
        ?: name
