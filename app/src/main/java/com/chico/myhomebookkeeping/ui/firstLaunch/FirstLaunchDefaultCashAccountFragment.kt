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
import com.chico.myhomebookkeeping.domain.DefaultCashAccountCatalog
import com.chico.myhomebookkeeping.enums.icon.names.CashAccountIconNames
import com.chico.myhomebookkeeping.obj.AppLanguage
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
            selectedCashAccountName = defaultCashAccount?.canonicalName
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
        viewModel.saveDefaultCashAccount(selectedDefaultCashAccount.canonicalName)
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
        val languageTag = AppLanguage.getSelectedTag(requireContext())
        return DefaultCashAccountCatalog.accounts.map { account ->
            getDefaultCashAccount(
                icon = iconResourceFor(account.iconName),
                account = account,
                languageTag = languageTag
            )
        }
    }

    private fun getDefaultCashAccount(
        icon: LiveData<Int>,
        account: DefaultCashAccountCatalog.Account,
        languageTag: String
    ): FirstLaunchSetupItem {
        return FirstLaunchSetupItem(
            img = icon.value ?: R.drawable.no_image,
            name = account.displayName(languageTag),
            canonicalName = account.canonicalName,
            nameRu = account.nameRu,
            namePl = account.namePl,
            nameDe = account.nameDe,
            nameBe = account.nameBe,
            nameBeLatn = account.nameBeLatn
        )
    }

    private fun updateCashAccounts() {
        val cashAccounts = getDefaultCashAccounts()
        viewModel.saveSelectedCashAccounts(cashAccounts)
        val selectedName = defaultCashAccount?.canonicalName
        defaultCashAccount = cashAccounts.firstOrNull { it.canonicalName == selectedName }
            ?: cashAccounts.first()
        adapter.updateCashAccounts(cashAccounts, defaultCashAccount?.canonicalName)
    }

    private fun iconResourceFor(iconName: CashAccountIconNames): LiveData<Int> {
        return when (iconName) {
            CashAccountIconNames.Card -> viewModel.cardCashAccountItem
            CashAccountIconNames.Cash -> viewModel.cashCashAccountItem
            CashAccountIconNames.CardOff -> viewModel.cardCashAccountItem
        }
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
                        cashAccount.canonicalName == selectedCashAccountName
                    selectCashAccountAsDefaultItem.setOnClickListener {
                        selectCashAccount(cashAccount)
                    }
                }
            }

            private fun selectCashAccount(cashAccount: FirstLaunchSetupItem) {
                val previousName = selectedCashAccountName
                val currentPosition = adapterPosition
                selectedCashAccountName = cashAccount.canonicalName
                onCashAccountSelected(cashAccount)
                cashAccounts.indexOfFirst { it.canonicalName == previousName }
                    .takeIf { it >= 0 }
                    ?.let { notifyItemChanged(it) }
                currentPosition
                    .takeIf { it >= 0 }
                    ?.let { notifyItemChanged(it) }
            }
        }
    }
}
