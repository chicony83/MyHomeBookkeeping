package com.chico.myhomebookkeeping.ui.cashAccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.chico.myhomebookkeeping.databinding.FragmentCashAccountBinding
import com.chico.myhomebookkeeping.db.dao.CashAccountDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.domain.CashAccountsUseCase

class CashAccountFragment : Fragment() {
//class CashAccountFragment : Fragment(), CashAccountAdapter.OnCashAccountListener {

    private lateinit var cashAccountViewModel: CashAccountViewModel
    private var _binding: FragmentCashAccountBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: CashAccountDao

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        db = dataBase.getDataBase(requireContext()).cashAccountDao()
        _binding = FragmentCashAccountBinding.inflate(inflater, container, false)

        cashAccountViewModel = ViewModelProvider(this).get(CashAccountViewModel::class.java)

        cashAccountViewModel.cashAccountList.observe(viewLifecycleOwner, {
            binding.cashAccountHolder.adapter = CashAccountAdapter(it,cashAccountViewModel)

            cashAccountViewModel.loadCashAccounts()
        })



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cashAccountsUseCase = CashAccountsUseCase()
        binding.showHideAddCashAccountFragment.setOnClickListener {
            if (binding.addNewCashAccountFragment.visibility == View.GONE) {
                binding.addNewCashAccountFragment.visibility = View.VISIBLE
            } else binding.addNewCashAccountFragment.visibility = View.GONE
        }
        binding.addNewCashAccountButton.setOnClickListener {
            if (binding.addNewCashAccountFragment.visibility == View.VISIBLE) {
                if (binding.cashAccountName.text.isNotEmpty()) {
                    val name = binding.cashAccountName.text.toString()
                    var number: Int? = null
                    if (binding.cashAccountNumber.text.isNotEmpty()) {
                        number = binding.cashAccountNumber.text.toString().toInt()
                    }
                    val newCashAccount = CashAccount(name, number)
                    cashAccountsUseCase.addNewCashAccount(db, newCashAccount)
                }
            }
        }
        Toast.makeText(context, "cash Account names", Toast.LENGTH_SHORT).show()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    override fun onClick() {
//        Toast.makeText(context, "click", Toast.LENGTH_SHORT).show()
//    }
}