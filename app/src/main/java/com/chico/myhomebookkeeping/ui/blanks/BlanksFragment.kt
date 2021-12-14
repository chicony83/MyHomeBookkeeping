package com.chico.myhomebookkeeping.ui.blanks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.chico.myhomebookkeeping.databinding.FragmentBlanksBinding

class BlanksFragment:Fragment() {
    private var _binding:FragmentBlanksBinding?=null
    private val binding get() = _binding!!

    private lateinit var blanksViewModel:BlanksViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBlanksBinding.inflate(inflater,container,false)

        with(blanksViewModel){

        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}