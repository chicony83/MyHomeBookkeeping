package com.chico.myhomebookkeeping.ui.fastPaymentsPackage.fastPayments

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
import com.chico.myhomebookkeeping.databinding.FragmentFastPaymentsBinding
import com.chico.myhomebookkeeping.helpers.NavControlHelper
import com.chico.myhomebookkeeping.interfaces.*
import com.chico.myhomebookkeeping.ui.fastPaymentsPackage.fastPayments.dialogs.SelectPaymentDialog
import com.chico.myhomebookkeeping.utils.launchIo
import com.chico.myhomebookkeeping.utils.launchUi
import com.google.android.material.bottomnavigation.BottomNavigationView

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
                binding.recyclerView.adapter = it?.let { it1 ->
                    FastPaymentsAdapter(it1, object :
                        OnItemViewClickListenerLong {
                        override fun onClick(selectedId: Long) {
                            showSelectDialog(selectedId)
                        }
                    })
                }
            })
        }
        return binding.root
    }

    private fun showSelectDialog(selectedId: Long) {
        launchIo {
            val fastPayment = fastPaymentsViewModel.loadSelectedFullFastPayment(selectedId)
            launchUi {
                val dialog = SelectPaymentDialog(fastPayment,
                    object : OnItemSelectForChangeCallBackLong {
                        override fun onSelect(id: Long) {
                            fastPaymentsViewModel.saveIdFastPaymentForChange(id)
                            navControlHelper.toSelectedFragment(R.id.nav_change_fast_payment_fragment)
                        }
                    },
                    object : OnItemSelectForSelectCallBackLong {
                        override fun onSelect(id: Long) {
                            fastPaymentsViewModel.saveARGSForPay(fastPayment)
                            navControlHelper.toSelectedFragment(R.id.nav_new_money_moving)
                        }
                    }
                )
                dialog.show(childFragmentManager, getString(R.string.tag_show_dialog))
            }
        }
    }

    override fun onStart() {
        super.onStart()
        fastPaymentsViewModel.getFullFastPaymentsList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            newBlankButton.setOnClickListener { navControlHelper.toSelectedFragment(R.id.nav_new_fast_payment_fragment) }
        }
        fastPaymentsViewModel.cleaningSP()

        if (navControlHelper.isPreviousFragment(R.id.nav_first_launch_fragment)){
            fastPaymentsViewModel.reloadRecycler()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showMessage(s: String) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show()
    }
}