package com.chico.myhomebookkeeping.ui.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.FragmentHelpBinding
import com.chico.myhomebookkeeping.helpers.NavControlHelper
import com.chico.myhomebookkeeping.utils.hideKeyboard

class HelpFragment : Fragment() {
    private var _binding: FragmentHelpBinding? = null
    private val binding get() = _binding!!
    private lateinit var control: NavController
    private lateinit var navControlHelper: NavControlHelper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHelpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.hideKeyboard()
        control = activity?.findNavController(R.id.nav_host_fragment)!!

        navControlHelper = NavControlHelper(control)

        when (control.previousBackStackEntry?.destination?.id) {
            R.id.nav_money_moving -> setTextInSomeTextArea("помощь с движением денег")
            R.id.nav_new_money_moving -> setTextInSomeTextArea("помощь с новой записью о движении денег")
            R.id.nav_cash_account -> setTextInSomeTextArea("помощь со счетами")
            R.id.nav_categories -> setTextInSomeTextArea("помощь с категориями")
            R.id.nav_currencies -> setTextInSomeTextArea("помощь с валютами")
            R.id.nav_setting -> setTextInSomeTextArea("помощь с настройками")
        }
        binding.submitButton.setOnClickListener {
            presSubmitButton()
        }

    }

    private fun setTextInSomeTextArea(text: String) {
        binding.someText.text = text
    }

    private fun presSubmitButton() {
        navControlHelper.moveToPreviousPage()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}