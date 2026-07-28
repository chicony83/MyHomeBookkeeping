package com.chico.myhomebookkeeping.ui.firstLaunch

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.ui.firstLaunch.firstLaunchSelectCurrenciesFragment.FirstLaunchSelectCurrenciesFragment
import com.chico.myhomebookkeeping.ui.firstLaunch.firstLaunchSelectCurrenciesFragment.FirstLaunchSelectCurrenciesViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FirstLaunchSetupFragment : Fragment(R.layout.fragment_first_launch_setup) {
    private val currenciesViewModel: FirstLaunchSelectCurrenciesViewModel by viewModels()
    private val firstLaunchViewModel: FirstLaunchViewModel by viewModels()
    private var installMode = FirstLaunchInstallMode.DEFAULT
    private var totalSteps = DEFAULT_SETUP_STEPS
    private var currentStep = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Toast.makeText(
                        requireContext(),
                        R.string.message_complete_first_launch_setup,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
        setInstallMode(
            savedInstanceState?.getString(KEY_INSTALL_MODE)?.let {
                FirstLaunchInstallMode.valueOf(it)
            } ?: firstLaunchViewModel.getSavedInstallMode()
        )
        currentStep = savedInstanceState?.getInt(KEY_CURRENT_STEP) ?: currentStep
        if (savedInstanceState == null) {
            showLanguageStep()
        } else {
            updateStepUi()
        }
        view.findViewById<Button>(R.id.firstLaunchStepButton).setOnClickListener {
            submitCurrentStep()
        }
        lifecycleScope.launchWhenStarted {
            currenciesViewModel.onDefaultCurrencyAdded.collect {
                showDefaultCashAccountStep()
            }
        }
    }

    fun setInstallMode(mode: FirstLaunchInstallMode) {
        installMode = mode
        totalSteps = when (mode) {
            FirstLaunchInstallMode.DEFAULT -> DEFAULT_SETUP_STEPS
            FirstLaunchInstallMode.CUSTOM -> CUSTOM_SETUP_STEPS
            FirstLaunchInstallMode.CLEAN_TRANSFER -> CLEAN_TRANSFER_SETUP_STEPS
        }
        updateStepUi()
    }

    // The mode is also stored in SP so Activity recreation after language changes
    // can continue the selected setup path.
    fun getInstallMode(): FirstLaunchInstallMode {
        return installMode
    }

    private fun showLanguageStep() {
        val fragment = FirstLaunchLanguageFragment()
        currentStep = 1
        childFragmentManager.beginTransaction()
            .replace(R.id.firstLaunchStepContainer, fragment)
            .setPrimaryNavigationFragment(fragment)
            .commit()
        updateStepUi()
    }

    fun showInstallModeStep() {
        val fragment = FirstLaunchInstallModeFragment()
        currentStep = 2
        childFragmentManager.beginTransaction()
            .replace(R.id.firstLaunchStepContainer, fragment)
            .setPrimaryNavigationFragment(fragment)
            .commit()
        updateStepUi()
    }

    fun showCurrenciesStep() {
        val fragment = FirstLaunchSelectCurrenciesFragment()
        currentStep = 3
        childFragmentManager.beginTransaction()
            .replace(R.id.firstLaunchStepContainer, fragment)
            .setPrimaryNavigationFragment(fragment)
            .commit()
        updateStepUi()
    }

    fun showCategoriesStep() {
        val fragment = FirstLaunchCategoriesFragment()
        currentStep = 6
        childFragmentManager.beginTransaction()
            .replace(R.id.firstLaunchStepContainer, fragment)
            .setPrimaryNavigationFragment(fragment)
            .commit()
        updateStepUi()
    }

    fun showDefaultCashAccountStep() {
        val fragment = FirstLaunchDefaultCashAccountFragment()
        currentStep = if (installMode == FirstLaunchInstallMode.DEFAULT) 4 else 5
        childFragmentManager.beginTransaction()
            .replace(R.id.firstLaunchStepContainer, fragment)
            .setPrimaryNavigationFragment(fragment)
            .commit()
        updateStepUi()
    }

    fun showStartDestinationStep() {
        val fragment = FirstLaunchStartDestinationFragment()
        currentStep = 7
        childFragmentManager.beginTransaction()
            .replace(R.id.firstLaunchStepContainer, fragment)
            .setPrimaryNavigationFragment(fragment)
            .commit()
        updateStepUi()
    }

    fun completeDefaultInstall() {
        firstLaunchViewModel.saveAllDefaultCategoryGroups()
        firstLaunchViewModel.saveStartFragment(Constants.START_FRAGMENT_CATEGORIES)
        firstLaunchViewModel.addSavedFirstLaunchElements()
        firstLaunchViewModel.clearSavedInstallMode()
        firstLaunchViewModel.setIsFirstLaunchFalse()
        finishFirstLaunch(R.id.nav_money_moving)
    }

    fun completeCleanTransferInstall() {
        lifecycleScope.launch {
            firstLaunchViewModel.installTechnicalIconDictionaries()
            firstLaunchViewModel.saveStartFragment(Constants.START_FRAGMENT_CATEGORIES)
            firstLaunchViewModel.setCleanInstallMessagePending()
            firstLaunchViewModel.clearSavedInstallMode()
            firstLaunchViewModel.setIsFirstLaunchFalse()
            finishFirstLaunch(R.id.nav_money_moving)
        }
    }

    fun finishFirstLaunch(destinationId: Int = R.id.nav_fast_payments_fragment) {
        findNavController().navigate(
            destinationId,
            null,
            NavOptions.Builder()
                .setPopUpTo(R.id.nav_first_launch_setup_fragment, true)
                .build()
        )
    }

    private fun submitCurrentStep() {
        when (val stepFragment = childFragmentManager.primaryNavigationFragment) {
            is FirstLaunchLanguageFragment -> stepFragment.submitStep()
            is FirstLaunchInstallModeFragment -> stepFragment.submitStep()
            is FirstLaunchSelectCurrenciesFragment -> stepFragment.submitStep()
            is FirstLaunchDefaultCurrencyFragment -> stepFragment.submitStep()
            is FirstLaunchCategoriesFragment -> stepFragment.submitStep()
            is FirstLaunchDefaultCashAccountFragment -> stepFragment.submitStep()
            is FirstLaunchStartDestinationFragment -> stepFragment.submitStep()
        }
    }

    fun showDefaultCurrencyStep() {
        val fragment = FirstLaunchDefaultCurrencyFragment()
        currentStep = 4
        childFragmentManager.beginTransaction()
            .replace(R.id.firstLaunchStepContainer, fragment)
            .setPrimaryNavigationFragment(fragment)
            .commit()
        updateStepUi()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(KEY_CURRENT_STEP, currentStep)
        outState.putString(KEY_INSTALL_MODE, installMode.name)
        super.onSaveInstanceState(outState)
    }

    private fun updateStepUi() {
        view?.findViewById<TextView>(R.id.firstLaunchStepTitle)?.text =
            getString(R.string.first_launch_setup_step_title, currentStep, totalSteps)
        view?.findViewById<Button>(R.id.firstLaunchStepButton)?.setText(
            if (currentStep == totalSteps) {
                R.string.first_launch_setup_done
            } else {
                R.string.first_launch_setup_next
            }
        )
    }

    companion object {
        private const val KEY_CURRENT_STEP = "currentFirstLaunchStep"
        private const val KEY_INSTALL_MODE = "currentFirstLaunchInstallMode"
        private const val DEFAULT_SETUP_STEPS = 4
        private const val CUSTOM_SETUP_STEPS = 7
        private const val CLEAN_TRANSFER_SETUP_STEPS = 2
    }
}
