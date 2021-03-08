package com.chico.myhomebookkeeping.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.chico.myhomebookkeeping.databinding.FragmentCategoriesBinding
import com.chico.myhomebookkeeping.db.dao.IncomeDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.recyclerView.IncomingCategoryAdapter
import com.chico.myhomebookkeeping.ui.alertdialog.AddCategoryFragment

class CategoriesFragment : Fragment() {

    private lateinit var categoriesViewModel: CategoriesViewModel
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: IncomeDao

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        db = dataBase.getDataBase(requireContext()).incomeDao()
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)

        categoriesViewModel = ViewModelProvider(this).get(CategoriesViewModel::class.java)

        categoriesViewModel.incomeCategoryList.observe(viewLifecycleOwner, {
            binding.incomeCategoryHolder.adapter = IncomingCategoryAdapter(it)

            categoriesViewModel.loadCategories()

        })

        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addIncomeCategory.setOnClickListener {
            val addCategoryFragment = AddCategoryFragment()
            val manager = childFragmentManager

            addCategoryFragment.show(manager, "add category")
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}