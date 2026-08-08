package com.chico.myhomebookkeeping.ui.firstLaunch

import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.obj.AppLanguage
import com.chico.myhomebookkeeping.obj.Constants

class FirstLaunchLanguageFragment : Fragment(R.layout.fragment_first_launch_language) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val radioGroup = view.findViewById<RadioGroup>(R.id.firstLaunchLanguageRadioGroup)
        radioGroup.removeAllViews()

        val selectedLanguage = AppLanguage.getSelectedTag(requireContext())
            .takeIf { it != Constants.APP_LANGUAGE_SYSTEM }
            ?: Constants.APP_LANGUAGE_ENGLISH

        AppLanguage.firstLaunchLanguages.forEach { language ->
            val radioButton = RadioButton(
                ContextThemeWrapper(requireContext(), R.style.Description_CashAccounts_FirstLaunch)
            ).apply {
                id = View.generateViewId()
                tag = language.tag
                text = getString(language.titleRes)
                layoutParams = RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.MATCH_PARENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT
                )
            }
            radioGroup.addView(radioButton)
            radioButton.isChecked = language.tag == selectedLanguage
        }
    }

    fun submitStep() {
        val activity = requireActivity()
        val context = requireContext()
        val radioGroup = requireView().findViewById<RadioGroup>(R.id.firstLaunchLanguageRadioGroup)
        val languageTag = radioGroup.findViewById<RadioButton>(radioGroup.checkedRadioButtonId)
            ?.tag as? String
            ?: Constants.APP_LANGUAGE_ENGLISH
        val previousLanguageTag = AppLanguage.getSelectedTag(context)

        AppLanguage.saveSelectedTag(context, languageTag)
        AppLanguage.applyLanguageTag(languageTag)
        (parentFragment as? FirstLaunchSetupFragment)?.showInstallModeStep()
        if (languageTag != previousLanguageTag) {
            activity.recreate()
        }
    }
}
