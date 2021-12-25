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
                aboutFastPayment.text = fastPayments.aboutFastPayment
                amount.text = fastPayments.amount.toString()
                ratingImg.setImageDrawable(getRatingImage(fastPayments.rating))

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