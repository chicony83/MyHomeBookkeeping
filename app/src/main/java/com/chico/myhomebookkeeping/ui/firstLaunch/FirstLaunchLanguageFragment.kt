package com.chico.myhomebookkeeping.ui.firstLaunch

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.obj.AppLanguage
import com.chico.myhomebookkeeping.obj.Constants

class FirstLaunchLanguageFragment : Fragment(R.layout.fragment_first_launch_language) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val selectedLanguage = AppLanguage.getSelectedTag(requireContext())
        view.findViewById<RadioButton>(R.id.firstLaunchEnglishRadioButton).isChecked =
            selectedLanguage == Constants.APP_LANGUAGE_ENGLISH ||
                    selectedLanguage == Constants.APP_LANGUAGE_SYSTEM
        view.findViewById<RadioButton>(R.id.firstLaunchPolishRadioButton).isChecked =
            selectedLanguage == Constants.APP_LANGUAGE_POLISH
        view.findViewById<RadioButton>(R.id.firstLaunchRussianRadioButton).isChecked =
            selectedLanguage == Constants.APP_LANGUAGE_RUSSIAN
    }

    fun submitStep() {
        val languageTag = when {
            requireView().findViewById<RadioButton>(R.id.firstLaunchPolishRadioButton).isChecked ->
                Constants.APP_LANGUAGE_POLISH
            requireView().findViewById<RadioButton>(R.id.firstLaunchRussianRadioButton).isChecked ->
                Constants.APP_LANGUAGE_RUSSIAN
            else -> Constants.APP_LANGUAGE_ENGLISH
        }

        AppLanguage.saveSelectedTag(requireContext(), languageTag)
        AppLanguage.applyLanguageTag(languageTag)
        (parentFragment as? FirstLaunchSetupFragment)?.showInstallModeStep()
    }
}
