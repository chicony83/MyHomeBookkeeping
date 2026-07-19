package com.chico.myhomebookkeeping.ui.firstLaunch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.databinding.FragmentFirstLaunchDefaultCashAccountBinding
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemSelectCashAccountAsDefaultDialogBinding

class FirstLaunchDefaultCashAccountFragment : Fragment() {
    private var _binding: FragmentFirstLaunchDefaultCashAccountBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FirstLaunchViewModel by viewModels({ requireParentFragment() })
    private var defaultCashAccount: FirstLaunchSetupItem? = null

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
        val selectedCashAccounts = viewModel.getSelectedCashAccounts()
        defaultCashAccount = selectedCashAccounts.firstOrNull()
        binding.defaultCashAccountHolder.adapter = SelectDefaultCashAccountAdapter(
            cashAccounts = selectedCashAccounts,
            selectedCashAccountName = defaultCashAccount?.name
        ) {
            defaultCashAccount = it
        }
    }

    fun submitStep() {
        val selectedDefaultCashAccount = defaultCashAccount ?: return
        viewModel.saveDefaultCashAccount(selectedDefaultCashAccount.name)
        (parentFragment as? FirstLaunchSetupFragment)?.showStartDestinationStep()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class SelectDefaultCashAccountAdapter(
        private val cashAccounts: List<FirstLaunchSetupItem>,
        selectedCashAccountName: String?,
        private val onCashAccountSelected: (FirstLaunchSetupItem) -> Unit
    ) : RecyclerView.Adapter<SelectDefaultCashAccountAdapter.ViewHolder>() {
        private var selectedCashAccountName = selectedCashAccountName

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
