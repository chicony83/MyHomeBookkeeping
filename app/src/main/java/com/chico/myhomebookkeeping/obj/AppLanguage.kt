package com.chico.myhomebookkeeping.obj

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.chico.myhomebookkeeping.R

object AppLanguage {
    data class Language(
        val tag: String,
        val titleRes: Int,
        val isSystem: Boolean = false,
        val showOnFirstLaunch: Boolean = true
    )

    val supportedLanguages = listOf(
        Language(
            tag = Constants.APP_LANGUAGE_SYSTEM,
            titleRes = R.string.settings_app_language_system,
            isSystem = true,
            showOnFirstLaunch = false
        ),
        Language(
            tag = Constants.APP_LANGUAGE_ENGLISH,
            titleRes = R.string.settings_app_language_english
        ),
        Language(
            tag = Constants.APP_LANGUAGE_POLISH,
            titleRes = R.string.settings_app_language_polish
        ),
        Language(
            tag = Constants.APP_LANGUAGE_RUSSIAN,
            titleRes = R.string.settings_app_language_russian
        )
    )

    val supportedTags = supportedLanguages.map { it.tag }
    val firstLaunchLanguages = supportedLanguages.filter { it.showOnFirstLaunch }

    fun getSelectedTag(context: Context): String {
        return context.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE)
            .getString(Constants.APP_LANGUAGE, Constants.APP_LANGUAGE_ENGLISH)
            ?.takeIf { supportedTags.contains(it) }
            ?: Constants.APP_LANGUAGE_ENGLISH
    }

    fun saveSelectedTag(context: Context, languageTag: String) {
        context.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(Constants.APP_LANGUAGE, languageTag.takeIf { supportedTags.contains(it) }
                ?: Constants.APP_LANGUAGE_ENGLISH)
            .apply()
    }

    fun applySelectedLanguage(context: Context) {
        applyLanguageTag(getSelectedTag(context))
    }

    fun applyLanguageTag(languageTag: String) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageTag))
    }

    fun findLanguage(languageTag: String): Language {
        return supportedLanguages.firstOrNull { it.tag == languageTag }
            ?: supportedLanguages.first { it.tag == Constants.APP_LANGUAGE_ENGLISH }
    }
}
