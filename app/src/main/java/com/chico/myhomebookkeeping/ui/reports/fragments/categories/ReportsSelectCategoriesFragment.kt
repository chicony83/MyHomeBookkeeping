package com.chico.myhomebookkeeping.ui.reports.fragments.categories

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
                binding.recyclerView.adapter =
                    getAdapter(it)
                getListSize(it)
            })
        }

        return binding.root
    }

    private fun getListSize(it: List<ReportsCategoriesItem>) {
        recyclerCashSize = it.size
    }

    private fun getAdapter(it: List<ReportsCategoriesItem>) =
        ReportsSelectCategoriesAdapter(it, recyclerViewState, object :OnItemCheckedCallBack{
            override fun onChecked(id: Int) {
                Message.log("checked id = $id")
            }

            override fun onUnChecked(id: Int) {
                Message.log("unchecked id = $id")
            }
        })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            recyclerView.setItemViewCacheSize(recyclerCashSize)

            resetButton.setOnClickListener {
                recyclerViewState = stateSelectNone
                updateAdapter()
            }
            selectAllButton.setOnClickListener {
                recyclerViewState = stateSelectAll
                updateAdapter()
            }
            selectAllIncomeButton.setOnClickListener {
                recyclerViewState = stateSelectAllIncome
                updateAdapter()
            }
            selectAllSpendingButton.setOnClickListener {
                recyclerViewState = stateSelectAllSpending
                updateAdapter()
            }

            submitButton.setOnClickListener { navControlHelper.moveToPreviousFragment() }

            cancelButton.setOnClickListener { navControlHelper.moveToPreviousFragment() }
        }
    }

    private fun updateAdapter() {
        reportsSelectCategoriesViewModel.categoriesItemsList.let {
            binding.recyclerView.adapter =
                it.value?.let { it1 -> getAdapter(it = it1.toList()) }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
//
//    private var stateCategoriesAdapter = mutableMapOf<String, Boolean>(
//        selectNone to false,
//        stateSelectAll to false,
//        stateSelectAllIncome to false,
//        stateSelectAllSpending to false
//    )

//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        return activity?.let { it ->
//
//            val builder = AlertDialog.Builder(it)
//            val inflater = requireActivity().layoutInflater
//            val layout = inflater.inflate(R.layout.dialog_select_category_from_reports, null)
//
//            recyclerView = layout.findViewById<RecyclerView>(R.id.recyclerView)
//
//            val selectNoneButton = layout.findViewById<Button>(R.id.selectNoneButton)
//            val selectAllButton = layout.findViewById<Button>(R.id.selectAllButton)
//            val selectAllIncomeButton = layout.findViewById<Button>(R.id.selectAllIncomeButton)
//            val selectAllSpendingButton = layout.findViewById<Button>(R.id.selectAllSpendingButton)
//
//            val cancelButton = layout.findViewById<Button>(R.id.cancelButton)
//            val submitButton = layout.findViewById<Button>(R.id.submitButton)
//
//            recyclerView.setItemViewCacheSize(categoriesList.size)
//
//            recyclerView.adapter = getAdapter(stateCategoriesAdapter, reportsCategoriesViewModel)
//
//            selectNoneButton.setOnClickListener {
//                reportsCategoriesViewModel.eraseSelectedCategories()
//                resetStateCategoriesAdapter(stateCategoriesAdapter)
//                recyclerView.adapter =
//                    getAdapter(stateCategoriesAdapter, reportsCategoriesViewModel)
//            }
//
//            selectAllButton.setOnClickListener {
//                reportsCategoriesViewModel.addAllInSelectedCategories()
//                resetStateCategoriesAdapter(stateCategoriesAdapter)
//                stateCategoriesAdapter[stateSelectAll] = true
//                recyclerView.adapter =
//                    getAdapter(stateCategoriesAdapter, reportsCategoriesViewModel)
//            }
//
//            selectAllIncomeButton.setOnClickListener {
//                reportsCategoriesViewModel.addAllIncomeInSelectedCategories()
//                resetStateCategoriesAdapter(stateCategoriesAdapter)
//                stateCategoriesAdapter[stateSelectAllIncome] = true
//                recyclerView.adapter =
//                    getAdapter(stateCategoriesAdapter, reportsCategoriesViewModel)
//            }
//
//            selectAllSpendingButton.setOnClickListener {
//                reportsCategoriesViewModel.addAllSpendingInSelectedCategories()
//                resetStateCategoriesAdapter(stateCategoriesAdapter)
//                stateCategoriesAdapter[stateSelectAllSpending] = true
//                recyclerView.adapter =
//                    getAdapter(stateCategoriesAdapter, reportsCategoriesViewModel)
//            }
//
//            submitButton.setOnClickListener {
//                onSelectedCategoriesCallBack.select(reportsCategoriesViewModel.getSelectedCategoriesSet())
//            }
//
//            cancelButton.setOnClickListener {
//            }
//
//            builder.setView(layout)
//            builder.create()
//
//        } ?: throw IllegalStateException(getString(R.string.exceptions_activity_cant_be_null))
//    }

//    override fun onStart() {
//        super.onStart()
//        resetStateCategoriesAdapter(stateCategoriesAdapter)
//        stateCategoriesAdapter[stateSelectAll] = true
//        recyclerView.adapter =
//            getAdapter(stateCategoriesAdapter, reportsCategoriesViewModel)
//    }
//
//    private fun resetStateCategoriesAdapter(stateCategoriesAdapter: MutableMap<String, Boolean>) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            stateCategoriesAdapter.replaceAll { _, _ -> false }
//        } else {
//            stateCategoriesAdapter.values.forEach {
//                false
//            }
//        }
//    }
//
//    private fun getAdapter(
//        stateCategoriesAdapter: MutableMap<String, Boolean>,
//        reportsCategoriesViewModel: ReportsSelectCategoriesViewModel
//    ) =
//        ReportsSelectCategoriesAdapter(
//            stateCategoriesAdapter,
//            reportsCategoriesViewModel.getCategoriesList(),
//            object : OnItemCheckedCallBack {
//                override fun onChecked(id: Int) {
//                    Message.log("checked id = $id")
//                    reportsCategoriesViewModel.addCategoryInCategoriesSet(id)
//                }
//
//                override fun onUnChecked(id: Int) {
//                    Message.log("unchecked id = $id")
//                    reportsCategoriesViewModel.deleteCategoryInCategoriesSet(id)
//                }
//
//            })
//

//    private fun dialogCancel() {
//        dialog?.cancel()
//    }
}