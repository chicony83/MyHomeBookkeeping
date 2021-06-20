package com.chico.myhomebookkeeping.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.FragmentSettingsBinding
import com.chico.myhomebookkeeping.helpers.ControlHelper

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var controlHelper: ControlHelper
    private lateinit var control: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        control = findNavController()
        controlHelper = ControlHelper(control)
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        with(binding) {
            changePasswordButton.setOnClickListener {
                controlHelper.moveToSelectedFragment(R.id.nav_select_password)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}