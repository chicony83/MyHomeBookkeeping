package com.chico.myhomebookkeeping.ui.firstLaunch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.FragmentFirstLaunchCategoriesBinding
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemFirstLaunchCategoryGroupBinding
import com.chico.myhomebookkeeping.domain.DefaultCategoryCatalog
import com.chico.myhomebookkeeping.domain.DefaultCategoryGroup

class FirstLaunchCategoriesFragment : Fragment(R.layout.fragment_first_launch_categories) {
    private var _binding: FragmentFirstLaunchCategoriesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FirstLaunchViewModel by viewModels({ requireParentFragment() })
    private lateinit var adapter: DefaultCategoryGroupsAdapter

    override fun onViewCreated(view: View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFirstLaunchCategoriesBinding.bind(view)
        adapter = DefaultCategoryGroupsAdapter(DefaultCategoryCatalog.groups)
        binding.defaultCategoriesHolder.adapter = adapter
    }

    fun submitStep() {
        viewModel.saveSelectedCategoryGroups(adapter.getSelectedCategoryGroups())
        (parentFragment as? FirstLaunchSetupFragment)?.showStartDestinationStep()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class DefaultCategoryGroupsAdapter(
        groups: List<DefaultCategoryGroup>
    ) : RecyclerView.Adapter<DefaultCategoryGroupsAdapter.ViewHolder>() {
        private val items = groups.map {
            SelectableDefaultCategoryGroup(
                group = it,
                isSelected = it.isSelectedByDefault
            )
        }.toMutableList()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = RecyclerViewItemFirstLaunchCategoryGroupBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size

        fun getSelectedCategoryGroups(): List<FirstLaunchCategoryGroupItem> {
            return items
                .filter { it.isSelected }
                .map {
                    FirstLaunchCategoryGroupItem(
                        parentName = it.group.parentName,
                        isIncome = it.group.isIncome,
                        subcategories = it.group.subcategories
                    )
                }
        }

        inner class ViewHolder(
            private val binding: RecyclerViewItemFirstLaunchCategoryGroupBinding
        ) : RecyclerView.ViewHolder(binding.root) {
            fun bind(item: SelectableDefaultCategoryGroup) {
                with(binding) {
                    defaultCategoryName.text = item.group.parentName
                    defaultCategorySubcategoryCount.text = itemView.context.getString(
                        R.string.first_launch_categories_subcategory_count,
                        item.group.subcategories.size
                    )
                    defaultCategoryCheckBox.isChecked = item.isSelected
                    defaultCategoryItem.setOnClickListener {
                        toggleSelection()
                    }
                }
            }

            private fun toggleSelection() {
                val position = adapterPosition
                if (position == RecyclerView.NO_POSITION) return
                items[position] = items[position].copy(isSelected = !items[position].isSelected)
                notifyItemChanged(position)
            }
        }
    }

    private data class SelectableDefaultCategoryGroup(
        val group: DefaultCategoryGroup,
        val isSelected: Boolean
    )
}
