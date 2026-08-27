package com.chico.myhomebookkeeping.ui.timePeriod

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.FragmentTimePeriodBinding
import com.chico.myhomebookkeeping.helpers.NavControlHelper
import com.chico.myhomebookkeeping.obj.Constants
import com.google.android.material.datepicker.MaterialDatePicker

class TimePeriodFragment : Fragment() {
    private lateinit var timePeriodViewModel: TimePeriodViewModel
    private var _binding: FragmentTimePeriodBinding? = null
    private val binding get() = _binding!!
    private lateinit var control: NavController
    private lateinit var navControlHelper: NavControlHelper
    private val textLogDataPicker = "TAG data picker"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimePeriodBinding.inflate(inflater, container, false)

        timePeriodViewModel = ViewModelProvider(this).get(TimePeriodViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        control = activity?.findNavController(R.id.nav_host_fragment)!!
        navControlHelper = NavControlHelper(controller = control)

        with(binding) {
            submitButton.setOnClickListener { pressSubmitButton() }
            customPeriodButton.setOnClickListener { showDateRangePicker() }
            allTimeButton.setOnClickListener {
                timePeriodViewModel.setTimePeriodMode(Constants.TIME_PERIOD_MODE_ALL_TIME)
            }
            thisWeekButton.setOnClickListener {
                timePeriodViewModel.setTimePeriodMode(Constants.TIME_PERIOD_MODE_THIS_WEEK)
            }
            lastWeekButton.setOnClickListener {
                timePeriodViewModel.setTimePeriodMode(Constants.TIME_PERIOD_MODE_LAST_WEEK)
            }
            thisMonthButton.setOnClickListener {
                timePeriodViewModel.setTimePeriodMode(Constants.TIME_PERIOD_MODE_THIS_MONTH)
            }
            lastMonthButton.setOnClickListener {
                timePeriodViewModel.setTimePeriodMode(Constants.TIME_PERIOD_MODE_LAST_MONTH)
            }
            last28DaysButton.setOnClickListener {
                timePeriodViewModel.setTimePeriodMode(Constants.TIME_PERIOD_MODE_LAST_28_DAYS)
            }
            last30DaysButton.setOnClickListener {
                timePeriodViewModel.setTimePeriodMode(Constants.TIME_PERIOD_MODE_LAST_30_DAYS)
            }
            last90DaysButton.setOnClickListener {
                timePeriodViewModel.setTimePeriodMode(Constants.TIME_PERIOD_MODE_LAST_90_DAYS)
            }
            last180DaysButton.setOnClickListener {
                timePeriodViewModel.setTimePeriodMode(Constants.TIME_PERIOD_MODE_LAST_180_DAYS)
            }
            last365DaysButton.setOnClickListener {
                timePeriodViewModel.setTimePeriodMode(Constants.TIME_PERIOD_MODE_LAST_365_DAYS)
            }
        }
        with(timePeriodViewModel) {
            startTimePeriodText.observe(viewLifecycleOwner) {
                binding.selectStartPeriodTextView.text = it
            }
            endTimePeriodText.observe(viewLifecycleOwner) {
                binding.selectEndPeriodTextView.text = it
            }
            selectedModeText.observe(viewLifecycleOwner) {
                binding.selectedModeTextView.text = it
                updateSelectedPeriodButton()
            }
            setTextOnButtons(navControlHelper)
        }
    }

    private fun showDateRangePicker() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText(getString(R.string.text_on_button_time_period_custom))

        val period = timePeriodViewModel.getResolvedTimePeriod()
        if (period.startTime > 0 && period.endTime > 0) {
            builder.setSelection(
                Pair(
                    TimePeriodResolver.localMillisToUtcDateSelection(period.startTime),
                    TimePeriodResolver.localMillisToUtcDateSelection(period.endTime)
                )
            )
        }

        val datePicker = builder.build()
        datePicker.addOnPositiveButtonClickListener { selectedRange ->
            val startDate = selectedRange.first ?: return@addOnPositiveButtonClickListener
            val endDate = selectedRange.second ?: return@addOnPositiveButtonClickListener
            timePeriodViewModel.setCustomTimePeriod(
                TimePeriodResolver.utcDateSelectionToLocalDayStart(startDate),
                TimePeriodResolver.utcDateSelectionToLocalDayEnd(endDate)
            )
        }
        datePicker.show(parentFragmentManager, textLogDataPicker)
    }

    private fun updateSelectedPeriodButton() {
        val selectedMode = timePeriodViewModel.getTimePeriodMode()
        periodButtonsByMode().forEach { (mode, button) ->
            button.isSelected = mode == selectedMode
        }
    }

    private fun periodButtonsByMode() = mapOf(
        Constants.TIME_PERIOD_MODE_CUSTOM to binding.customPeriodButton,
        Constants.TIME_PERIOD_MODE_ALL_TIME to binding.allTimeButton,
        Constants.TIME_PERIOD_MODE_THIS_WEEK to binding.thisWeekButton,
        Constants.TIME_PERIOD_MODE_LAST_WEEK to binding.lastWeekButton,
        Constants.TIME_PERIOD_MODE_THIS_MONTH to binding.thisMonthButton,
        Constants.TIME_PERIOD_MODE_LAST_MONTH to binding.lastMonthButton,
        Constants.TIME_PERIOD_MODE_LAST_28_DAYS to binding.last28DaysButton,
        Constants.TIME_PERIOD_MODE_LAST_30_DAYS to binding.last30DaysButton,
        Constants.TIME_PERIOD_MODE_LAST_90_DAYS to binding.last90DaysButton,
        Constants.TIME_PERIOD_MODE_LAST_180_DAYS to binding.last180DaysButton,
        Constants.TIME_PERIOD_MODE_LAST_365_DAYS to binding.last365DaysButton
    )

    private fun pressSubmitButton() {
        timePeriodViewModel.saveARGStoSP(navControlHelper)
        navControlHelper.moveToPreviousFragment()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
