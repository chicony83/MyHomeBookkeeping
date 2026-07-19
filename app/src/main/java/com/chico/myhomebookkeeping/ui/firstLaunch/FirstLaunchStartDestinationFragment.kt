package com.chico.myhomebookkeeping.ui.firstLaunch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.FragmentFirstLaunchStartDestinationBinding
import com.chico.myhomebookkeeping.obj.Constants

class FirstLaunchStartDestinationFragment : Fragment() {
    private var _binding: FragmentFirstLaunchStartDestinationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FirstLaunchViewModel by viewModels({ requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstLaunchStartDestinationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.startFastPaymentsRadioButton.isChecked = true
    }

    fun submitStep() {
        val startFragment = when (binding.startDestinationRadioGroup.checkedRadioButtonId) {
            R.id.startCategoriesRadioButton -> Constants.START_FRAGMENT_CATEGORIES
            R.id.startJournalRadioButton -> Constants.START_FRAGMENT_JOURNAL
            else -> Constants.START_FRAGMENT_FAST_PAYMENTS
        }
        viewModel.saveStartFragment(startFragment)
        viewModel.addSavedFirstLaunchElements()
        viewModel.setIsFirstLaunchFalse()
        (parentFragment as? FirstLaunchSetupFragment)?.finishFirstLaunch(
            viewModel.getStartFragmentDestinationId()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
