package com.chico.myhomebookkeeping.ui.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.db.entity.IconsResource
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.interfaces.OnItemSelectForSelectCallBackInt
import java.lang.IllegalStateException

class SelectIconDialog(
    private val iconsList: List<IconsResource>,
    private val onItemSelectForSelectCallBackInt: OnItemSelectForSelectCallBackInt
) : DialogFragment() {

    @SuppressLint("NewApi")
    var displayWidth: Int = 0
    var selectedIconId: Int = 0

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

            selectButton.setOnClickListener {
                if (selectedIconId > 0) {
                    onItemSelectForSelectCallBackInt.onSelect(selectedIconId)
                    dialogCancel()
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.message_icon_is_not_selected),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }

            cancelButton.setOnClickListener {
                dialogCancel()
            }

            builder.create()
        } ?: throw  IllegalStateException(getString(R.string.exceptions_activity_cant_be_null))
    }

    private fun dialogCancel() {
        dialog?.cancel()
    }


    @SuppressLint("ResourceType", "UseCompatLoadingForDrawables")
    private fun buildLayout(layout: View) {
        val parentLayout = layout.findViewById<GridLayout>(R.id.iconsLayout)

        val height: Int = displayWidth / 8
        val width: Int = displayWidth / 8

        val param = RelativeLayout.LayoutParams(height, width)
        val margin = displayWidth / 32

        param.topMargin = margin
        param.leftMargin = margin
        param.bottomMargin = margin
        param.rightMargin = margin

        var prevImgId: Int = 0

        for (i in iconsList.indices) {
            val imageView = ImageView(requireContext())
            imageView.setImageResource(iconsList[i].iconResources)

            val presetBorder = resources.getDrawable(R.drawable.border, null)
            val nullBorder = resources.getDrawable(R.drawable.border_null, null)

            imageView.id = iconsList[i].id?.toInt() ?: 0

            imageView.setOnClickListener {
                imageView.background = presetBorder
                Message.log("pressed id  = ${imageView.id.toString()}")

                if (prevImgId > 0) {
                    Message.log("prev img ID = $prevImgId")
                    val prevImg: ImageView = imageView.rootView.findViewById(prevImgId)
                    prevImg.background = nullBorder
                    prevImgId = imageView.id
                }
                if (prevImgId == 0) {
                    prevImgId = imageView.id
                    Message.log("set first prev img ID = ${prevImgId.toInt()}")
                }
                selectedIconId = imageView.id
            }
            parentLayout.addView(imageView, param)
        }
    }
}