package com.chico.myhomebookkeeping.ui.fastPaymentsPackage.fastPayments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemFastPaymentBinding
import com.chico.myhomebookkeeping.db.entity.FastPayments

class FastPaymentsAdapter(
    private val fastPaymentsList: List<FastPayments>
) : RecyclerView.Adapter<FastPaymentsAdapter.ViewHolderFastPaymentItem>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderFastPaymentItem {
        val binding = RecyclerViewItemFastPaymentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolderFastPaymentItem(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderFastPaymentItem, position: Int) {
        holder.bind(fastPaymentsList[position])
    }

    override fun getItemCount() = fastPaymentsList.size

    inner class ViewHolderFastPaymentItem(private val binding: RecyclerViewItemFastPaymentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(fastPayments: FastPayments) {
            with(binding) {
                descriptionFastPaymentTextView.text = fastPayments.aboutFastPayment

            }
        }

    }
}