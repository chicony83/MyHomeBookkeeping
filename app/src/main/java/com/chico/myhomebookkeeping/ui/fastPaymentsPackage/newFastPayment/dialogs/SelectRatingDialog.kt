package com.chico.myhomebookkeeping.ui.fastPaymentsPackage.newFastPayment.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.interfaces.fastPayments.OnSelectRatingValueCallBack
import java.lang.IllegalStateException

class SelectRatingDialog(val onSelectRatingValueCallBack: OnSelectRatingValueCallBack) :
    DialogFragment() {

    private lateinit var ratingOneImg: ImageView
    private lateinit var ratingTwoImg: ImageView
    private lateinit var ratingThreeImg: ImageView
    private lateinit var ratingFourImg: ImageView
    private lateinit var ratingFiveImg: ImageView
    private var _rating = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_seleect_rating_of_fast_payment, null)

            ratingOneImg = layout.findViewById<ImageView>(R.id.setRatingOneImg)
            ratingTwoImg = layout.findViewById<ImageView>(R.id.setRatingTwoImg)
            ratingThreeImg = layout.findViewById<ImageView>(R.id.setRatingThreeImg)
            ratingFourImg = layout.findViewById<ImageView>(R.id.setRatingFourImg)
            ratingFiveImg = layout.findViewById<ImageView>(R.id.setRatingFiveImg)

            val cancelButton = layout.findViewById<Button>(R.id.cancelButton)
            val submitButton = layout.findViewById<Button>(R.id.submitButton)

            ratingOneImg.setOnClickListener { setRatingAndImage(0) }
            ratingTwoImg.setOnClickListener { setRatingAndImage(1) }
            ratingThreeImg.setOnClickListener { setRatingAndImage(2) }
            ratingFourImg.setOnClickListener { setRatingAndImage(3) }
            ratingFiveImg.setOnClickListener { setRatingAndImage(4) }

            submitButton.setOnClickListener {
                onSelectRatingValueCallBack.select(_rating)
                cancelDialog()
            }
            cancelButton.setOnClickListener {
                cancelDialog()
            }

            builder.setView(layout)
            builder.create()

        } ?: throw IllegalStateException(getString(R.string.exceptions_activity_cant_be_null))
    }

    private fun cancelDialog() {
        dialog?.cancel()
    }

    private fun setRatingAndImage(rating: Int) {
        _rating = rating
        setImages(_rating)
    }

    private fun setImages(rating: Int) {
        rating.let {
            when (it) {
                0 -> setRatingStars(1)
                1 -> setRatingStars(2)
                2 -> setRatingStars(3)
                3 -> setRatingStars(4)
                4 -> setRatingStars(5)
            }
        }
    }

    private fun setRatingStars(rating: Int) {
        var fullStarsNum = rating
        val listImg = listOf<ImageView>(
            ratingOneImg,
            ratingTwoImg,
            ratingThreeImg,
            ratingFourImg,
            ratingFiveImg
        )
        for (i in listImg.indices) {
            if (fullStarsNum > 0) listImg[i].setImageResource(R.drawable.full_star)
            else if (fullStarsNum <= 0) listImg[i].setImageResource(R.drawable.empty_star)
            fullStarsNum--
        }
    }
}