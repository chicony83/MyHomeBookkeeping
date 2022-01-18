package com.chico.myhomebookkeeping.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.db.entity.IconsResource
import com.chico.myhomebookkeeping.helpers.Message
import java.lang.IllegalStateException

class SelectIconDialog(
    private val iconsList: List<IconsResource>
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { it ->
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_select_icon, null)
            buildLayout(layout)
            builder.setView(layout)

            val selectButton = layout.findViewById<Button>(R.id.selectButton)
            val cancelButton = layout.findViewById<Button>(R.id.cancelButton)

            cancelButton.setOnClickListener {
                dialogCancel()
            }

            builder.create()
        } ?: throw  IllegalStateException(getString(R.string.exceptions_activity_cant_be_null))
    }

    private fun dialogCancel() {
        dialog?.cancel()
    }

    private fun buildLayout(layout: View) {
        val iconsLayout = layout.findViewById<LinearLayout>(R.id.iconsLayout)
        for (i in iconsList.indices){
            val imageView:ImageView = ImageView(requireContext())
            imageView.setImageResource(iconsList[i].iconResources)
            imageView.setOnClickListener {
                Message.log("press on image")
            }
            iconsLayout.addView(imageView)
        }
    }
}