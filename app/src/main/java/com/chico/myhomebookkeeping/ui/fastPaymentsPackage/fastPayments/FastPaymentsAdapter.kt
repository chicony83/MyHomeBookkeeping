package com.chico.myhomebookkeeping.ui.fastPaymentsPackage.fastPayments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemAddPaymentBinding
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemFastPaymentBinding
import com.chico.myhomebookkeeping.db.full.FullFastPayment
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.interfaces.OnItemViewClickListenerLong
import com.chico.myhomebookkeeping.interfaces.OnPressCreateNewElement
import com.chico.myhomebookkeeping.interfaces.fastPayments.OnLongClickListenerCallBack

class FastPaymentsAdapter(
    private val context: Context,
    private val onShortClick: OnItemViewClickListenerLong,
    private val onCreateNewElementClick: OnPressCreateNewElement,
    private val onLongClick: OnLongClickListenerCallBack

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val ADD_FAST_PAYMENT = "Add fast payment P4k5k2K436nk4754jkXh53"
        const val FAST_PAYMENT_VH_ID = 1
        const val ADD_FAST_PAYMENT_VH_ID = 0
    }

    private var initList: List<FullFastPayment> = emptyList()

    fun updateList(newList: List<FullFastPayment>?) {
        if (newList != null) {
            initList = newList
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == FAST_PAYMENT_VH_ID) {
            ViewHolderFastPaymentItem(
                RecyclerViewItemFastPaymentBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            ViewHolderAddPaymentItem(
                RecyclerViewItemAddPaymentBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemCount() = initList.size

    override fun getItemViewType(position: Int): Int {
        return if (initList[position].nameFastPayment == ADD_FAST_PAYMENT) ADD_FAST_PAYMENT_VH_ID else FAST_PAYMENT_VH_ID
    }

    inner class ViewHolderAddPaymentItem(private val binding: RecyclerViewItemAddPaymentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.addNewElementImg.setOnClickListener {
                onCreateNewElementClick.onPress()
                Message.log("---PreSSEd---")
            }
        }
    }

    inner class ViewHolderFastPaymentItem(private val binding: RecyclerViewItemFastPaymentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("CheckResult")
        fun bind(fastPayment: FullFastPayment) {
            with(binding) {
                itemId.text = fastPayment.id.toString()
                nameFastPayment.text = fastPayment.nameFastPayment
                cashAccountName.text = fastPayment.cashAccountNameValue
                currencyName.text = fastPayment.currencyNameValue
                descriptionOfPayment.text = fastPayment.description
                ratingImg.setImageDrawable(getRatingImage(fastPayment.rating))

                categoryName.text = fastPayment.categoryNameValue

                if (fastPayment.amount.toString().isNotEmpty()) {
                    val number: Double = fastPayment.amount ?: 0.0
                    if (number > 0) {
                        amount.text = fastPayment.amount.toString()
                    }
                    if (number <= 0) {
                        amount.text = "-"
                    }
                }
                if (fastPayment.description.isNullOrEmpty()) {
                    binding.descriptionOfPayment.visibility = View.GONE
                }

                fastPaymentItemId.setOnClickListener { _ ->
                    fastPayment.id.let { it ->

                        if (fastPayment.childCategories.getOrNull(0)?.nameRes != null) {
                            MaterialDialog(context).show {
                                title(R.string.fragment_label_select_categories)
                                listItemsSingleChoice(items = fastPayment.childCategories.map {
                                    binding.root.context.getString(
                                        it.nameRes ?: 0
                                    )
                                }) { dialog, index, text ->
                                    onShortClick.onClick(
                                        fastPayment,
                                        fastPayment.childCategories[index]
                                    )
                                }
                                positiveButton(R.string.text_on_button_submit)
                            }
                        } else {
                            onShortClick.onClick(fastPayment, fastPayment.childCategories[0])
                        }
                    }
                }
                fastPaymentItemId.setOnLongClickListener {
                    onLongClick.longClick(fastPayment.id)
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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == FAST_PAYMENT_VH_ID) {
            (holder as ViewHolderFastPaymentItem).bind(initList[position])
        } else {
            (holder as ViewHolderAddPaymentItem).bind()
        }
    }
}