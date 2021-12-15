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

    private lateinit var ratingImage: ImageView
    private lateinit var seekBar: SeekBar

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_seleect_rating_of_fast_payment, null)

            seekBar = layout.findViewById(R.id.seekBar)
            val setRatingOneText = layout.findViewById<TextView>(R.id.setRatingOneTextView)
            val setRatingTwoText = layout.findViewById<TextView>(R.id.setRatingTwoTextView)
            val setRatingThreeText = layout.findViewById<TextView>(R.id.setRatingThreeTextView)
            val setRatingFourText = layout.findViewById<TextView>(R.id.setRatingFourTextView)
            val setRatingFiveText = layout.findViewById<TextView>(R.id.setRatingFiveTextView)
            val cancelButton = layout.findViewById<Button>(R.id.cancelButton)
            val submitButton = layout.findViewById<Button>(R.id.submitButton)

            ratingImage = layout.findViewById(R.id.ratingStarsImageView)

            seekBar.max = 50
            setSeekBarProgress(25)

            seekBar.setOnSeekBarChangeListener(this)
            setRatingOneText.setOnClickListener { setProgressAndImage(0, R.drawable.rating1) }
            setRatingTwoText.setOnClickListener { setProgressAndImage(13, R.drawable.rating2) }
            setRatingThreeText.setOnClickListener { setProgressAndImage(25, R.drawable.rating3) }
            setRatingFourText.setOnClickListener { setProgressAndImage(37, R.drawable.rating4) }
            setRatingFiveText.setOnClickListener { setProgressAndImage(50, R.drawable.rating5) }

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

    private fun setProgressAndImage(progress: Int, ratingImage: Int) {
        setSeekBarProgress(progress)
        setImage(ratingImage)
    }

    private fun setSeekBarProgress(progress: Int) {
        seekBar.progress = progress
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

        progress.let {
            when (it) {
                in 0..9 -> setImage(R.drawable.rating1)
                in 10..19 -> setImage(R.drawable.rating2)
                in 20..29 -> setImage(R.drawable.rating3)
                in 30..39 -> setImage(R.drawable.rating4)
                in 40..50 -> setImage(R.drawable.rating5)
            }
        }
    }


    private fun setImage(img: Int) {
        ratingImage.setImageResource(img)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }
}