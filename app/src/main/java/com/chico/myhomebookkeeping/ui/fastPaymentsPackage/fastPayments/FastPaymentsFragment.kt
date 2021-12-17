package com.chico.myhomebookkeeping.ui.fastPaymentsPackage.fastPayments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.FragmentFastPaymentsBinding
import com.chico.myhomebookkeeping.helpers.NavControlHelper

class FastPaymentsFragment : Fragment() {
    private var _binding: FragmentFastPaymentsBinding? = null
    private val binding get() = _binding!!

    private lateinit var control: NavController
    private lateinit var navControlHelper: NavControlHelper

    private lateinit var fastPaymentsViewModel: FastPaymentsViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFastPaymentsBinding.inflate(inflater, container, false)
        control = activity?.findNavController(R.id.nav_host_fragment)!!
        navControlHelper = NavControlHelper(controller = control)

        fastPaymentsViewModel = ViewModelProvider(this).get(FastPaymentsViewModel::class.java)
        with(fastPaymentsViewModel) {
            fastPaymentsList.observe(viewLifecycleOwner, {
                binding.recyclerView.adapter = FastPaymentsAdapter(it)
            })
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            newBlankButton.setOnClickListener { navControlHelper.toSelectedFragment(R.id.nav_new_fast_money_moving_fragment) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}