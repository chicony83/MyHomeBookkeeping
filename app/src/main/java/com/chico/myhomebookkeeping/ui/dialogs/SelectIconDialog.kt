package com.chico.myhomebookkeeping.ui.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context.WINDOW_SERVICE
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.text.BoringLayout
import android.view.Display
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.db.entity.IconsResource
import com.chico.myhomebookkeeping.helpers.Message
import java.lang.IllegalStateException

class SelectIconDialog(
    private val iconsList: List<IconsResource>
) : DialogFragment() {

    @SuppressLint("NewApi")
    var displayWidth: Int = 0


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { it ->
            displayWidth = context?.display?.width ?: 640
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


    @SuppressLint("ResourceType")
    private fun buildLayout(layout: View) {
        val parentLayout = layout.findViewById<GridLayout>(R.id.iconsLayout)

        val height: Int = displayWidth / 8
        val width: Int = displayWidth / 8

        val param = RelativeLayout.LayoutParams(height, width)
        val margin = displayWidth/32
        param.topMargin = margin
        param.leftMargin = margin
        param.bottomMargin = margin
        param.rightMargin = margin

        for (i in iconsList.indices) {
            val imageView = ImageView(requireContext())
            imageView.setImageResource(iconsList[i].iconResources)
            imageView.tag = iconsList[i].id
            imageView.setOnClickListener {
                Message.log("press on image ${imageView.tag}")
            }

            parentLayout.addView(imageView, param)
        }
    }
}