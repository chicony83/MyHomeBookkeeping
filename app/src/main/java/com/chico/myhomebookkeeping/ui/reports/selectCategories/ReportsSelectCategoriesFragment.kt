package com.chico.myhomebookkeeping.ui.reports.selectCategories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.FragmentReportsSelectCategoryBinding
import com.chico.myhomebookkeeping.enums.StatesReportsCategoriesAdapter
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.helpers.NavControlHelper
import com.chico.myhomebookkeeping.interfaces.OnItemCheckedCallBack

class ReportsSelectCategoriesFragment(
) : Fragment() {

    private val stateSelectNone: String = StatesReportsCategoriesAdapter.SelectNone.name
    private val stateSelectAll: String = StatesReportsCategoriesAdapter.SelectAll.name
    private val stateSelectAllIncome: String = StatesReportsCategoriesAdapter.SelectAllIncome.name
    private val stateSelectAllSpending: String =
        StatesReportsCategoriesAdapter.SelectAllSpending.name

    private var recyclerViewState = stateSelectAll

    private var recyclerCashSize = 0

    private var _binding: FragmentReportsSelectCategoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var control: NavController
    private lateinit var navControlHelper: NavControlHelper
    private lateinit var reportsSelectCategoriesViewModel: ReportsSelectCategoriesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportsSelectCategoryBinding.inflate(inflater, container, false)

        control = activity?.findNavController(R.id.nav_host_fragment)!!
        navControlHelper = NavControlHelper(control)

        reportsSelectCategoriesViewModel =
            ViewModelProvider(this).get(ReportsSelectCategoriesViewModel::class.java)

        with(reportsSelectCategoriesViewModel) {
            categoriesItemsList.observe(viewLifecycleOwner, {
                binding.recyclerView.adapter = getAdapter(it)
//                getListSize(it)
                binding.recyclerView.setItemViewCacheSize(it.size)
            })
//            printResult()
        }
//        binding.recyclerView.setItemViewCacheSize(recyclerCashSize)
        return binding.root
    }

    private fun getListSize(it: List<ReportsCategoriesItem>) {
        recyclerCashSize = it.size
        Message.log(" size = $recyclerCashSize")
    }

    private fun getAdapter(it: List<ReportsCategoriesItem>) =
        ReportsSelectCategoriesAdapter(
            it,
            recyclerViewState,
            reportsSelectCategoriesViewModel.getSelectedCategoriesFromSp(),
            object : OnItemCheckedCallBack {
                override fun onChecked(id: Int) {
                    reportsSelectCategoriesViewModel.setCategoryChecked(id)
//                recyclerViewState = stateSelectNone
//                reportsSelectCategoriesViewModel.printResult()
                }

                override fun onUnChecked(id: Int) {
//                Message.log("unchecked id = $id")
                    reportsSelectCategoriesViewModel.setCategoryUnChecked(id)
//                recyclerViewState = stateSelectNone
//                reportsSelectCategoriesViewModel.printResult()
                }
            })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
//            recyclerView.setItemViewCacheSize(recyclerCashSize)

            resetButton.setOnClickListener {
                reportsSelectCategoriesViewModel.newSelectedCategoriesSetSp()
                reportsSelectCategoriesViewModel.clearSelectedCategories()
                recyclerViewState = stateSelectNone
                updateAdapter()
//                reportsSelectCategoriesViewModel.printResult()
            }
            selectAllButton.setOnClickListener {
                reportsSelectCategoriesViewModel.newSelectedCategoriesSetSp()
                recyclerViewState = stateSelectAll
                updateAdapter()
//                reportsSelectCategoriesViewModel.printResult()
            }
            selectAllIncomeButton.setOnClickListener {
                reportsSelectCategoriesViewModel.newSelectedCategoriesSetSp()
                reportsSelectCategoriesViewModel.clearSelectedCategories()
                recyclerViewState = stateSelectAllIncome
                updateAdapter()
//                reportsSelectCategoriesViewModel.printResult()
            }
            selectAllSpendingButton.setOnClickListener {
                reportsSelectCategoriesViewModel.newSelectedCategoriesSetSp()
                reportsSelectCategoriesViewModel.clearSelectedCategories()
                recyclerViewState = stateSelectAllSpending
                updateAdapter()
//                reportsSelectCategoriesViewModel.printResult()
            }

            submitButton.setOnClickListener {
                reportsSelectCategoriesViewModel.saveSelectedCategories()
                navControlHelper.moveToPreviousFragment()
            }

            cancelButton.setOnClickListener { navControlHelper.moveToPreviousFragment() }
        }
    }

    private fun updateAdapter() {
        reportsSelectCategoriesViewModel.categoriesItemsList.let {
            binding.recyclerView.adapter =
                it.value?.let { it1 -> getAdapter(it = it1.toList()) }
        }
        reportsSelectCategoriesViewModel.categoriesItemsList.value?.forEach {
            Message.log("$it")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
    }
}