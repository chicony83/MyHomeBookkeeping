package com.chico.myhomebookkeeping.ui.selectPassword

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.chico.myhomebookkeeping.databinding.FragmentSelectPasswordBinding
import com.chico.myhomebookkeeping.helpers.NavControlHelper
import com.chico.myhomebookkeeping.helpers.UiHelper


class SelectPasswordFragment : Fragment() {

    private lateinit var selectedPasswordViewModel: SelectPasswordViewModel

    private var _binding: FragmentSelectPasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var navControlHelper: NavControlHelper
    private lateinit var control: NavController

    private var passwordString = ""
    private var repeatPasswordString = ""
    private lateinit var validatePassword: Validate
    private var blockedSubmitButton = true
    private val uiHelper = UiHelper()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        selectedPasswordViewModel = ViewModelProvider(this).get(SelectPasswordViewModel::class.java)
        control = findNavController()
        navControlHelper = NavControlHelper(control)
        _binding = FragmentSelectPasswordBinding.inflate(inflater, container, false)
        validatePassword = Validate(selectedPasswordViewModel)

        with(selectedPasswordViewModel) {
            passwordMessage.observe(viewLifecycleOwner, {
                binding.passwordMessage.text = it
            })
            promptPass.observe(viewLifecycleOwner, {
                binding.promptText.setText(it)
            })
        }

        with(binding) {
            selectPasswordText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    passwordString = selectPasswordText.text.toString()
                    checkAndValidate()
                }

                override fun afterTextChanged(s: Editable?) {
                }

            })
            repeatPasswordText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    repeatPasswordString = repeatPasswordText.text.toString()
                    checkAndValidate()
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })

            submitButton.setOnClickListener {
                selectedPasswordViewModel.save(
                    selectPasswordText.text.toString(),
                    promptText.text.toString()
                )
                navControlHelper.moveToPreviousFragment()
            }

        }

        return binding.root
    }

    private fun checkAndValidate() {
        if (validatePassword.validate(passwordString, repeatPasswordString)) {
            uiHelper.unblockButton(binding.submitButton)
        } else {
            uiHelper.blockButton(binding.submitButton)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}