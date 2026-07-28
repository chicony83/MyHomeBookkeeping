package com.chico.myhomebookkeeping.ui.firstLaunch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.FragmentFirstLaunchDefaultCashAccountBinding
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemSelectCashAccountAsDefaultDialogBinding
import com.chico.myhomebookkeeping.utils.launchIo

class FirstLaunchDefaultCashAccountFragment : Fragment() {
    private var _binding: FragmentFirstLaunchDefaultCashAccountBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FirstLaunchViewModel by viewModels({ requireParentFragment() })
    private var defaultCashAccount: FirstLaunchSetupItem? = null
    private lateinit var adapter: SelectDefaultCashAccountAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstLaunchDefaultCashAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val selectedCashAccounts = getDefaultCashAccounts()
        viewModel.saveSelectedCashAccounts(selectedCashAccounts)
        defaultCashAccount = selectedCashAccounts.first()
        adapter = SelectDefaultCashAccountAdapter(
            cashAccounts = selectedCashAccounts,
            selectedCashAccountName = defaultCashAccount?.name
        ) {
            defaultCashAccount = it
        }
        binding.defaultCashAccountHolder.adapter = adapter

        viewModel.cardCashAccountItem.observe(viewLifecycleOwner) {
            updateCashAccounts()
        }
        viewModel.cashCashAccountItem.observe(viewLifecycleOwner) {
            updateCashAccounts()
        }
    }

    fun submitStep() {
        val selectedDefaultCashAccount = defaultCashAccount ?: return
        viewModel.saveSelectedCashAccounts(getDefaultCashAccounts())
        viewModel.saveDefaultCashAccount(selectedDefaultCashAccount.name)
        val setupFragment = parentFragment as? FirstLaunchSetupFragment
        if (setupFragment?.getInstallMode() == FirstLaunchInstallMode.DEFAULT) {
            setupFragment.completeDefaultInstall()
        } else {
            setupFragment?.showCategoriesStep()
        }
    }

    override fun onStart() {
        super.onStart()
        launchIo {
            viewModel.installTechnicalIconDictionaries()
            viewModel.updateValuesNow()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getDefaultCashAccounts(): List<FirstLaunchSetupItem> {
        return listOf(
            getDefaultCashAccount(viewModel.cardCashAccountItem, R.string.quick_setup_name_Card),
            getDefaultCashAccount(viewModel.cashCashAccountItem, R.string.quick_setup_name_Cash)
        )
    }

    private fun getDefaultCashAccount(
        icon: LiveData<Int>,
        nameRes: Int
    ): FirstLaunchSetupItem {
        return FirstLaunchSetupItem(
            img = icon.value ?: R.drawable.no_image,
            name = getString(nameRes)
        )
    }

    private fun updateCashAccounts() {
        val cashAccounts = getDefaultCashAccounts()
        viewModel.saveSelectedCashAccounts(cashAccounts)
        val selectedName = defaultCashAccount?.name
        defaultCashAccount = cashAccounts.firstOrNull { it.name == selectedName }
            ?: cashAccounts.first()
        adapter.updateCashAccounts(cashAccounts, defaultCashAccount?.name)
    }

    private class SelectDefaultCashAccountAdapter(
        cashAccounts: List<FirstLaunchSetupItem>,
        selectedCashAccountName: String?,
        private val onCashAccountSelected: (FirstLaunchSetupItem) -> Unit
    ) : RecyclerView.Adapter<SelectDefaultCashAccountAdapter.ViewHolder>() {
        private var cashAccounts: List<FirstLaunchSetupItem> = cashAccounts
        private var selectedCashAccountName = selectedCashAccountName

        fun updateCashAccounts(
            cashAccounts: List<FirstLaunchSetupItem>,
            selectedCashAccountName: String?
        ) {
            this.cashAccounts = cashAccounts
            this.selectedCashAccountName = selectedCashAccountName
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = RecyclerViewItemSelectCashAccountAsDefaultDialogBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(cashAccounts[position])
        }

        override fun getItemCount() = cashAccounts.size

        inner class ViewHolder(
            private val binding: RecyclerViewItemSelectCashAccountAsDefaultDialogBinding
        ) : RecyclerView.ViewHolder(binding.root) {
            fun bind(cashAccount: FirstLaunchSetupItem) {
                with(binding) {
                    iconImg.setImageResource(cashAccount.img)
                    nameCashAccount.text = cashAccount.name
                    defaultCashAccountRadioButton.isChecked =
                        cashAccount.name == selectedCashAccountName
                    selectCashAccountAsDefaultItem.setOnClickListener {
                        selectCashAccount(cashAccount)
                    }
                }
            }

            private fun selectCashAccount(cashAccount: FirstLaunchSetupItem) {
                val previousName = selectedCashAccountName
                val currentPosition = adapterPosition
                selectedCashAccountName = cashAccount.name
                onCashAccountSelected(cashAccount)
                cashAccounts.indexOfFirst { it.name == previousName }
                    .takeIf { it >= 0 }
                    ?.let { notifyItemChanged(it) }
                currentPosition
                    .takeIf { it >= 0 }
                    ?.let { notifyItemChanged(it) }
            }
        }
    }
}
