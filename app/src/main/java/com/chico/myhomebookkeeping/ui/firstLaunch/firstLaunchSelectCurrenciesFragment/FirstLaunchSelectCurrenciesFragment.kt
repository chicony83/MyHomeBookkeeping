package com.chico.myhomebookkeeping.ui.firstLaunch.firstLaunchSelectCurrenciesFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.chico.myhomebookkeeping.databinding.FragmentFirstLaunchBinding
import com.chico.myhomebookkeeping.databinding.FragmentFirstLaunchSelectCurrenciesBinding

class FirstLaunchSelectCurrenciesFragment:Fragment() {
    private var _binding:FragmentFirstLaunchSelectCurrenciesBinding? = null
    private val binding get() = _binding!!
    private lateinit var firstLaunchSelectCurrenciesViewModel: FirstLaunchSelectCurrenciesViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstLaunchSelectCurrenciesBinding.inflate(inflater, container, false)
        firstLaunchSelectCurrenciesViewModel = ViewModelProvider(this).get(FirstLaunchSelectCurrenciesViewModel::class.java)



        return binding.root
    }
}