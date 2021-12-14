package com.chico.myhomebookkeeping.ui.fastMoneyMovings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.FragmentFastMoneyMovingBinding
import com.chico.myhomebookkeeping.helpers.NavControlHelper

class FastMoneyMovingFragment : Fragment() {
    private var _binding: FragmentFastMoneyMovingBinding? = null
    private val binding get() = _binding!!

    private lateinit var control: NavController
    private lateinit var navControlHelper: NavControlHelper

    private lateinit var fastMoneyMovingViewModel: FastMoneyMovingViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFastMoneyMovingBinding.inflate(inflater, container, false)

        control = activity?.findNavController(R.id.nav_host_fragment)!!

        navControlHelper = NavControlHelper(controller = control)

        with(binding) {
            newBlankButton.setOnClickListener { navControlHelper.moveToSelectedFragment(R.id.nav_new_fast_money_moving_fragment) }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}