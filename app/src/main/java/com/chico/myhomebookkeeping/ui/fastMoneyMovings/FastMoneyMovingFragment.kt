package com.chico.myhomebookkeeping.ui.fastMoneyMovings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.chico.myhomebookkeeping.databinding.FragmentFastMoneyMovingBinding

class FastMoneyMovingFragment:Fragment() {
    private var _binding:FragmentFastMoneyMovingBinding?=null
    private val binding get() = _binding!!

    private lateinit var fastMoneyMovingViewModel:FastMoneyMovingViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFastMoneyMovingBinding.inflate(inflater,container,false)


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}