package com.chico.myhomebookkeeping.ui.timePeriod

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.FragmentTimePeriodBinding
import com.chico.myhomebookkeeping.helpers.NavControlHelper
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.*

class TimePeriodFragment : Fragment() {
    private lateinit var timePeriodViewModel: TimePeriodViewModel
    private var _binding: FragmentTimePeriodBinding? = null
    private val binding get() = _binding!!
    private lateinit var control: NavController
    private lateinit var navControlHelper: NavControlHelper
    private val datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("select date")
        .setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build()

    private var isGetStartTimePeriod = false
    private var isGetEndTimePeriod = false
    private val textLogDataPicker = "data picker"

    private val calendar = Calendar.getInstance()
    private val dateNowInMills = calendar.timeInMillis

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
            selectStartPeriodButton.setOnClickListener {
                isGetStartTimePeriod = true
                datePicker.show(parentFragmentManager, textLogDataPicker)
            }
            selectEndPeriodButton.setOnClickListener {
                isGetEndTimePeriod = true
                datePicker.show(parentFragmentManager, textLogDataPicker)
            }
            resetStartPeriodButton.setOnClickListener {
                timePeriodViewModel.resetStartPeriod()
                resetStartPeriodButton.isEnabled = false
            }
            resetEndPeriodButton.setOnClickListener {
                timePeriodViewModel.resetEndPeriod()
                resetEndPeriodButton.isEnabled = false
            }
        }
        with(timePeriodViewModel) {
            startTimePeriodText.observe(viewLifecycleOwner,{
                binding.selectStartPeriodButton.text = it
            })
            endTimePeriodText.observe(viewLifecycleOwner,{
                binding.selectEndPeriodButton.text = it
            })

//            startTimePeriodText.observe(viewLifecycleOwner, {
//                binding.startPeriodText.text = it
//            })
//            endTimePeriodText.observe(viewLifecycleOwner, {
//                binding.endPeriodText.text = it
//            })
        }
        datePicker.addOnPositiveButtonClickListener {
            if (isGetStartTimePeriod) {
                if (it < dateNowInMills) {
                    timePeriodViewModel.setStartTimePeriod(it)
                    binding.resetStartPeriodButton.isEnabled = true
                }
                if (it > dateNowInMills) {
                    message("начальная дата не может быть больше текущей")
                }
                isGetStartTimePeriod = false
            }
            if (isGetEndTimePeriod) {
                if (it < timePeriodViewModel.getStartTimePeriod()){
                    message("конечная дата не может быть больше начальной")
                }
                else{
                    binding.resetEndPeriodButton.isEnabled = true
                    timePeriodViewModel.setEndTimePeriod(it)
                }
                isGetEndTimePeriod = false
            }
        }
    }

    private fun message(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }

    private fun pressSubmitButton() {
        timePeriodViewModel.saveARGStoSP()
        navControlHelper.moveToPreviousPage()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}