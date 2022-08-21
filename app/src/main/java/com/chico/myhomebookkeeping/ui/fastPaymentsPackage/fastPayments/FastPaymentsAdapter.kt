package com.chico.myhomebookkeeping.ui.fastPaymentsPackage.fastPayments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemFastPaymentBinding
import com.chico.myhomebookkeeping.db.full.FullFastPayment
import com.chico.myhomebookkeeping.interfaces.OnItemViewClickListenerLong

class FastPaymentsAdapter(
    private val fullFastPaymentsList: List<FullFastPayment>,
    private val listener: OnItemViewClickListenerLong
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
        if (position <fullFastPaymentsList.size){
            holder.bind(fullFastPaymentsList[position])
        }
        else{
            holder.bindAddButton()
        }
    }

    override fun getItemCount() = fullFastPaymentsList.size +1

    inner class ViewHolderFastPaymentItem(private val binding: RecyclerViewItemFastPaymentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindAddButton() {
            with(binding){
                fastPaymentItemId.visibility = View.GONE
                addNewElementLayout.visibility = View.VISIBLE
            }
        }

        fun bind(fastPayments: FullFastPayment) {
            with(binding) {
                itemId.text = fastPayments.id.toString()
                nameFastPayment.text = fastPayments.nameFastPayment
                cashAccountName.text = fastPayments.cashAccountNameValue
                currencyName.text = fastPayments.currencyNameValue
                descriptionOfPayment.text = fastPayments.description
                ratingImg.setImageDrawable(getRatingImage(fastPayments.rating))

                categoryName.text = fastPayments.categoryNameValue
//                if (fastPayments.)

                if (fastPayments.amount.toString().isNotEmpty()) {
                    val number: Double = fastPayments.amount ?: 0.0
                    if (number > 0) {
                        amount.text = fastPayments.amount.toString()
                    }
                    if (number <= 0) {
                        amount.text = "-"
                    }
                }
                if (fastPayments.description?.toString().isNullOrEmpty()){
                    binding.descriptionOfPayment.visibility = View.GONE
                }

                fastPaymentItemId.setOnClickListener {
                    fastPayments.id.let { listener.onClick(it) }
                }
            }
        }

        private fun getRatingImage(rating: Int): Drawable? {
            return when (rating) {
                0 -> drawableRatingStars(R.drawable.rating1)
                1 -> drawableRatingStars(R.drawable.rating2)
                2 -> drawableRatingStars(R.drawable.rating3)
                3 -> drawableRatingStars(R.drawable.rating4)
                4 -> drawableRatingStars(R.drawable.rating5)
                else -> {
                    drawableRatingStars(R.drawable.rating1)
                }
            }
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        private fun drawableRatingStars(img: Int) = context.getDrawable(img)

    }
}