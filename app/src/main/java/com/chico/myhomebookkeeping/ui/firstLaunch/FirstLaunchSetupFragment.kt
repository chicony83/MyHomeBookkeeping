package com.chico.myhomebookkeeping.ui.firstLaunch

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.ui.firstLaunch.firstLaunchSelectCurrenciesFragment.FirstLaunchSelectCurrenciesFragment
import com.chico.myhomebookkeeping.ui.firstLaunch.firstLaunchSelectCurrenciesFragment.FirstLaunchSelectCurrenciesViewModel
import kotlinx.coroutines.flow.collect

class FirstLaunchSetupFragment : Fragment(R.layout.fragment_first_launch_setup) {
    private val viewModel: FirstLaunchSelectCurrenciesViewModel by viewModels()
    private val totalSteps = 5
    private var currentStep = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentStep = savedInstanceState?.getInt(KEY_CURRENT_STEP) ?: currentStep
        if (savedInstanceState == null) {
            showCurrenciesStep()
        } else {
            updateStepUi()
        }
        view.findViewById<Button>(R.id.firstLaunchStepButton).setOnClickListener {
            submitCurrentStep()
        }
        lifecycleScope.launchWhenStarted {
            viewModel.onDefaultCurrencyAdded.collect {
                showCashAccountsAndCategoriesStep()
            }
        }
    }

    private fun showCurrenciesStep() {
        val fragment = FirstLaunchSelectCurrenciesFragment()
        currentStep = 1
        childFragmentManager.beginTransaction()
            .replace(R.id.firstLaunchStepContainer, fragment)
            .setPrimaryNavigationFragment(fragment)
            .commit()
        updateStepUi()
    }

    fun showCashAccountsAndCategoriesStep() {
        val fragment = FirstLaunchFragment()
        currentStep = 3
        childFragmentManager.beginTransaction()
            .replace(R.id.firstLaunchStepContainer, fragment)
            .setPrimaryNavigationFragment(fragment)
            .commit()
        updateStepUi()
    }

    fun showDefaultCashAccountStep() {
        val fragment = FirstLaunchDefaultCashAccountFragment()
        currentStep = 4
        childFragmentManager.beginTransaction()
            .replace(R.id.firstLaunchStepContainer, fragment)
            .setPrimaryNavigationFragment(fragment)
            .commit()
        updateStepUi()
    }

    fun showStartDestinationStep() {
        val fragment = FirstLaunchStartDestinationFragment()
        currentStep = 5
        childFragmentManager.beginTransaction()
            .replace(R.id.firstLaunchStepContainer, fragment)
            .setPrimaryNavigationFragment(fragment)
            .commit()
        updateStepUi()
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
            is FirstLaunchSelectCurrenciesFragment -> stepFragment.submitStep()
            is FirstLaunchDefaultCurrencyFragment -> stepFragment.submitStep()
            is FirstLaunchFragment -> stepFragment.submitStep()
            is FirstLaunchDefaultCashAccountFragment -> stepFragment.submitStep()
            is FirstLaunchStartDestinationFragment -> stepFragment.submitStep()
        }
    }

    fun showDefaultCurrencyStep() {
        val fragment = FirstLaunchDefaultCurrencyFragment()
        currentStep = 2
        childFragmentManager.beginTransaction()
            .replace(R.id.firstLaunchStepContainer, fragment)
            .setPrimaryNavigationFragment(fragment)
            .commit()
        updateStepUi()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(KEY_CURRENT_STEP, currentStep)
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
    }
}
