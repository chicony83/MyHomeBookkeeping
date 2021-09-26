package com.chico.myhomebookkeeping.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.checks.GetVersionCode
import com.chico.myhomebookkeeping.databinding.FragmentSettingsBinding
import com.chico.myhomebookkeeping.helpers.NavControlHelper
import com.chico.myhomebookkeeping.helpers.UiHelper

class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var navControlHelper: NavControlHelper
    private lateinit var control: NavController
    private val uiHelper = UiHelper()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        control = findNavController()
        navControlHelper = NavControlHelper(control)
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)

        with(binding) {
//            changePasswordButton.setOnClickListener {
//                navControlHelper.moveToSelectedFragment(R.id.nav_select_password)
//            }
            quickSetupButton.setOnClickListener {
                navControlHelper.moveToSelectedFragment(R.id.nav_first_launch_fragment)
            }
            checkNewVersionButton.setOnClickListener {
                checkNewVersion()
            }
            submitButton.setOnClickListener {
                message("настройки сохранены")
                navControlHelper.moveToPreviousPage()
            }
        }
        with(settingsViewModel){
            appVersion.observe(viewLifecycleOwner, {
                binding.appVersion.text = it.toString()
            })
        }

        return binding.root
    }

    private fun checkNewVersion() {
        val getVersionCode = GetVersionCode(requireContext())
        val version: Any = getVersionCode.getNewVersion()
    }

    private fun message(text: String) {
        Toast.makeText(requireContext(),text,Toast.LENGTH_LONG).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}