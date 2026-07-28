package com.chico.myhomebookkeeping.ui.firstLaunch

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.chico.myhomebookkeeping.R

class FirstLaunchInstallModeFragment : Fragment(R.layout.fragment_first_launch_install_mode) {
    private val viewModel: FirstLaunchViewModel by viewModels({ requireParentFragment() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mode = viewModel.getSavedInstallMode()
        checkInstallMode(mode)
        view.findViewById<RadioButton>(R.id.firstLaunchDefaultInstallRadioButton)
            .setOnClickListener { saveMode(FirstLaunchInstallMode.DEFAULT) }
        view.findViewById<RadioButton>(R.id.firstLaunchCustomInstallRadioButton)
            .setOnClickListener { saveMode(FirstLaunchInstallMode.CUSTOM) }
        view.findViewById<RadioButton>(R.id.firstLaunchCleanTransferInstallRadioButton)
            .setOnClickListener { saveMode(FirstLaunchInstallMode.CLEAN_TRANSFER) }
    }

    fun submitStep() {
        val mode = viewModel.getSavedInstallMode()
        val setupFragment = parentFragment as? FirstLaunchSetupFragment
        setupFragment?.setInstallMode(mode)
        when (mode) {
            FirstLaunchInstallMode.CLEAN_TRANSFER -> setupFragment?.completeCleanTransferInstall()
            else -> setupFragment?.showCurrenciesStep()
        }
    }

    private fun saveMode(mode: FirstLaunchInstallMode) {
        viewModel.saveInstallMode(mode)
        (parentFragment as? FirstLaunchSetupFragment)?.setInstallMode(mode)
    }

    private fun checkInstallMode(mode: FirstLaunchInstallMode) {
        val checkedRadioButtonId = when (mode) {
            FirstLaunchInstallMode.DEFAULT -> R.id.firstLaunchDefaultInstallRadioButton
            FirstLaunchInstallMode.CUSTOM -> R.id.firstLaunchCustomInstallRadioButton
            FirstLaunchInstallMode.CLEAN_TRANSFER -> R.id.firstLaunchCleanTransferInstallRadioButton
        }
        requireView().findViewById<RadioButton>(checkedRadioButtonId).isChecked = true
    }
}
