package com.chico.myhomebookkeeping.ui.fastPaymentsPackage.fastPayments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemFastPaymentBinding
import com.chico.myhomebookkeeping.db.entity.FastPayments

class FastPaymentsAdapter(
    private val fastPaymentsList: List<FastPayments>
) : RecyclerView.Adapter<FastPaymentsAdapter.ViewHolderFastPaymentItem>() {


    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderFastPaymentItem {
        val binding = RecyclerViewItemFastPaymentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        context = parent.context

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
                amount.text = fastPayments.amount.toString()
                ratingVal.text = fastPayments.rating.toString()
                ratingImg.setImageDrawable(getRatingImage(fastPayments.rating))

            }
        }

        private fun getRatingImage(rating: Int): Drawable? {
            return when (rating) {
                in 0..9 -> drawable(R.drawable.rating1)
                in 10..19 -> drawable(R.drawable.rating2)
                in 20..29 -> drawable(R.drawable.rating3)
                in 30..39 -> drawable(R.drawable.rating4)
                else -> {
                    drawable(R.drawable.rating5)
                }
            }
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        private fun drawable(img: Int) = context.getDrawable(img)

    }
}