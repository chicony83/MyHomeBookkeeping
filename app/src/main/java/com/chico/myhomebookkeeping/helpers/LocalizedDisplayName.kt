package com.chico.myhomebookkeeping.helpers

import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.entity.FastPayments
import com.chico.myhomebookkeeping.db.entity.ParentCategories
import com.chico.myhomebookkeeping.obj.Constants

// English names are canonical in DB rows; Russian columns are optional localized display values.
fun CashAccount.displayName(languageTag: String): String =
    accountNameRu.takeIf { languageTag == Constants.APP_LANGUAGE_RUSSIAN && !it.isNullOrBlank() }
        ?: accountName

fun Categories.displayName(languageTag: String): String =
    categoryNameRu.takeIf { languageTag == Constants.APP_LANGUAGE_RUSSIAN && !it.isNullOrBlank() }
        ?: categoryName

fun ParentCategories.displayName(languageTag: String): String =
    nameRu.takeIf { languageTag == Constants.APP_LANGUAGE_RUSSIAN && !it.isNullOrBlank() }
        ?: name

fun FastPayments.displayName(languageTag: String): String =
    nameFastPaymentRu.takeIf { languageTag == Constants.APP_LANGUAGE_RUSSIAN && !it.isNullOrBlank() }
        ?: nameFastPayment

fun localizedName(name: String?, nameRu: String?, languageTag: String): String? =
    nameRu.takeIf { languageTag == Constants.APP_LANGUAGE_RUSSIAN && !it.isNullOrBlank() }
        ?: name
