package com.chico.myhomebookkeeping.ui.fastPaymentsPackage.fastPayments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.FragmentFastPaymentsBinding
import com.chico.myhomebookkeeping.enums.SortingFastPayments
import com.chico.myhomebookkeeping.enums.StateRecyclerFastPaymentByType
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.helpers.NavControlHelper
import com.chico.myhomebookkeeping.interfaces.*
import com.chico.myhomebookkeeping.interfaces.fastPayments.OnLongClickListenerCallBack
import com.chico.myhomebookkeeping.ui.fastPaymentsPackage.fastPayments.dialogs.SelectPaymentDialog
import com.chico.myhomebookkeeping.ui.dialogs.WhatNewInLastVersionDialog
import com.chico.myhomebookkeeping.utils.launchIo
import com.chico.myhomebookkeeping.utils.launchUi

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

        val layoutManager = GridLayoutManager(activity, 2)

        with(fastPaymentsViewModel) {
//            sortedByTextOnButton.observe(viewLifecycleOwner, {
//                binding.sortingButton.text = it
//            })

            fastPaymentsList.observe(viewLifecycleOwner) {
                binding.recyclerView.layoutManager = layoutManager
                binding.recyclerView.adapter = it?.let { it1 ->
                    FastPaymentsAdapter(
                        it1,
                        object : OnItemViewClickListenerLong {
                            override fun onClick(selectedId: Long) {
                                Message.log("---Short click detected---")
                                fastPaymentsViewModel.saveIdFastPaymentForPay(selectedId)
                                navControlHelper.toSelectedFragment(R.id.nav_new_money_moving)
                            }
                        },
                        object : OnClickCreateNewElementCallBack {
                            override fun onPress() {
                                navControlHelper.toSelectedFragment(R.id.nav_new_fast_payment_fragment)
                                Message.log("PRESS")
                            }
                        },
                        object : OnLongClickListenerCallBack {
                            override fun longClick(long: Long): Boolean {
                                Message.log("---LONG click detected  on ${long}---")
                                showSelectDialog(long)
                                return true
                            }

                            override fun onLongClick(p0: View?): Boolean {
                                return true
                            }
                        }
                    )
                }
            }
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

        if (!fastPaymentsViewModel.isLastVersionOfProgramChecked()) {
            val updateViewModel = ViewModelProvider(this).get(UpdateViewModel::class.java)
            updateViewModel.update()
//            addIconsInDataBase()
            showWhatsNewDialog()
            fastPaymentsViewModel.setLastVersionChecked()
        }
        updateIncomeSpendingAllButtons()
    }

    private fun updateIncomeSpendingAllButtons() {

        when (fastPaymentsViewModel.getTypeOfRecycler()) {
            StateRecyclerFastPaymentByType.Spending.name -> {
                setPressedSelectSpendingButton()
            }
            StateRecyclerFastPaymentByType.Income.name -> {
                setPressedSelectIncomeButton()
            }
            StateRecyclerFastPaymentByType.All.name -> {
                setPressedSelectAllButton()
            }
            else -> {
                setPressedSelectAllButton()
            }
        }
    }

//    private fun addIconsInDataBase() {
//        launchIo {
//            fastPaymentsViewModel.addIconsInDataBase()
//        }
//    }

    private fun showWhatsNewDialog() {
        val dialog = WhatNewInLastVersionDialog()
        dialog.show(childFragmentManager, getString(R.string.tag_show_dialog))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            selectAllButton.setOnClickListener {
                fastPaymentsViewModel.getAllFastPayments()
                setPressedSelectAllButton()
            }
            selectAllIncomeButton.setOnClickListener {
                fastPaymentsViewModel.getIncomeFastPayments()
                setPressedSelectIncomeButton()
            }
            selectAllSpendingButton.setOnClickListener {
                fastPaymentsViewModel.getSpendingFastPayments()
                setPressedSelectSpendingButton()
            }

            sortingButton.setOnClickListener {
                val popupMenu = PopupMenu(context, sortingButton)
                popupMenu.menuInflater.inflate(
                    R.menu.pop_up_menu_sorting_fast_payments,
                    popupMenu.menu
                )
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.sort_by_alphabet_ASC -> {
                            Message.log("sorting bu numbers ASC")
                            sortingFastPayments(SortingFastPayments.AlphabetByAsc.toString())
                        }
                        R.id.sort_by_alphabet_DESC -> {
                            Message.log("sorting bu numbers DESC")
                            sortingFastPayments(SortingFastPayments.AlphabetByDesc.toString())
                        }
                        R.id.sort_by_rating_ASK -> {
                            Message.log("sorting bu rating ASC")
                            sortingFastPayments(SortingFastPayments.RatingByAsc.toString())
                        }
                        R.id.sort_by_rating_DESC -> {
                            Message.log("sorting bu rating DESC")
                            sortingFastPayments(SortingFastPayments.RatingByDesc.toString())
                        }
                    }
                    true
                }
                popupMenu.show()
            }
//            newBlankButton.setOnClickListener { navControlHelper.toSelectedFragment(R.id.nav_new_fast_payment_fragment) }
        }
        fastPaymentsViewModel.cleaningSP()

        if (navControlHelper.isPreviousFragment(R.id.nav_first_launch_fragment)) {
            fastPaymentsViewModel.reloadRecycler()
        }
    }

    private fun setPressedSelectSpendingButton() {
        setPressed(binding.selectAllSpendingButton)
        setUnpressed(listOf(binding.selectAllIncomeButton, binding.selectAllButton))
    }

    private fun setPressedSelectIncomeButton() {
        setPressed(binding.selectAllIncomeButton)
        setUnpressed(listOf(binding.selectAllButton, binding.selectAllSpendingButton))
    }

    private fun setPressedSelectAllButton() {
        setPressed(binding.selectAllButton)
        setUnpressed(listOf(binding.selectAllIncomeButton, binding.selectAllSpendingButton))
    }

    private fun setPressed(pressedButton: Button) {
        pressedButton.isEnabled = false
    }

    private fun setUnpressed(listOfButtons: List<Button>) {
        for (i in listOfButtons.indices) {
            listOfButtons[i].isEnabled = true

        }
    }

    private fun sortingFastPayments(sorting: String) {
        with(fastPaymentsViewModel) {
            setSortingCategories(sorting)
            reloadRecycler()
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