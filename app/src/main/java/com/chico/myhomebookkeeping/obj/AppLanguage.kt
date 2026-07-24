package com.chico.myhomebookkeeping.obj

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object AppLanguage {
    val supportedTags = listOf(
        Constants.APP_LANGUAGE_SYSTEM,
        Constants.APP_LANGUAGE_ENGLISH,
        Constants.APP_LANGUAGE_RUSSIAN
    )

    fun getSelectedTag(context: Context): String {
        return context.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE)
            .getString(Constants.APP_LANGUAGE, Constants.APP_LANGUAGE_SYSTEM)
            ?.takeIf { supportedTags.contains(it) }
            ?: Constants.APP_LANGUAGE_SYSTEM
    }

    fun saveSelectedTag(context: Context, languageTag: String) {
        context.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(Constants.APP_LANGUAGE, languageTag.takeIf { supportedTags.contains(it) }
                ?: Constants.APP_LANGUAGE_SYSTEM)
            .apply()
    }

    fun applySelectedLanguage(context: Context) {
        applyLanguageTag(getSelectedTag(context))
    }

    fun applyLanguageTag(languageTag: String) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageTag))
    }
}
