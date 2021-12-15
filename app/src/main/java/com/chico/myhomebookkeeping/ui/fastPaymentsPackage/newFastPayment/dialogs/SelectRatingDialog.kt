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
    DialogFragment(), SeekBar.OnSeekBarChangeListener {


    private lateinit var seekBar: SeekBar
    private lateinit var ratingOneImg: ImageView
    private lateinit var ratingTwoImg: ImageView
    private lateinit var ratingThreeImg: ImageView
    private lateinit var ratingFourImg: ImageView
    private lateinit var ratingFiveImg: ImageView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_seleect_rating_of_fast_payment, null)

            seekBar = layout.findViewById(R.id.seekBar)

            ratingOneImg = layout.findViewById<ImageView>(R.id.setRatingOneImg)
            ratingTwoImg = layout.findViewById<ImageView>(R.id.setRatingTwoImg)
            ratingThreeImg = layout.findViewById<ImageView>(R.id.setRatingThreeImg)
            ratingFourImg = layout.findViewById<ImageView>(R.id.setRatingFourImg)
            ratingFiveImg = layout.findViewById<ImageView>(R.id.setRatingFiveImg)

            val cancelButton = layout.findViewById<Button>(R.id.cancelButton)
            val submitButton = layout.findViewById<Button>(R.id.submitButton)

            seekBar.max = 50
            setSeekBarProgress(25)

            seekBar.setOnSeekBarChangeListener(this)
            ratingOneImg.setOnClickListener { setProgressAndImage(0) }
            ratingTwoImg.setOnClickListener { setProgressAndImage(13) }
            ratingThreeImg.setOnClickListener { setProgressAndImage(25) }
            ratingFourImg.setOnClickListener { setProgressAndImage(37) }
            ratingFiveImg.setOnClickListener { setProgressAndImage(50) }

            submitButton.setOnClickListener {
                onSelectRatingValueCallBack.select(seekBar.progress)
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

    private fun setProgressAndImage(progress: Int) {
        setSeekBarProgress(progress)
        setImages(progress)
    }

    private fun setSeekBarProgress(progress: Int) {
        seekBar.progress = progress
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        setImages(progress)
    }


    private fun setImages(progress: Int) {
        progress.let {
            when (it) {
                in 0..9 -> setRatingStars(1)
                in 10..19 -> setRatingStars(2)
                in 20..29 -> setRatingStars(3)
                in 30..39 -> setRatingStars(4)
                in 40..50 -> setRatingStars(5)
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

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }
}