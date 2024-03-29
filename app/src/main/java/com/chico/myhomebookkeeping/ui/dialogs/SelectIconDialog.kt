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
import com.chico.myhomebookkeeping.interfaces.OnSelectIconCallBack
import java.lang.IllegalStateException

class SelectIconDialog(
    private val iconsList: List<IconsResource>,
    private val onSelectIconCallBack: OnSelectIconCallBack
//    private val onItemSelectForSelectCallBackInt: OnItemSelectForSelectCallBackInt
) : DialogFragment() {

    @SuppressLint("NewApi")
    var displayWidth: Int = 0
    var selectedIconId: Long = 0

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
                    iconsList.find {
                        selectedIconId == it.id
                    }?.let { it1 ->
                        onSelectIconCallBack.selectIcon(icon = it1)
                    }
//                    onItemSelectForSelectCallBackInt.onSelect(selectedIconId)
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

        Message.log("icons list size in select icon dialog = ${iconsList.size}")
        val parentLayout = layout.findViewById<GridLayout>(R.id.iconsHolderLayout)

        val height: Int = displayWidth / 8
        val width: Int = displayWidth / 8

        val param = RelativeLayout.LayoutParams(height, width)
        val margin: Int = displayWidth / 32

        //param.topMargin = margin
        param.leftMargin = margin
        param.bottomMargin = margin
        param.rightMargin = margin

        var prevImgId = 0

        for (i in iconsList.indices) {
            Message.log("---drawing icon $i")
            val imageView = ImageView(requireContext())
            imageView.setImageResource(iconsList[i].iconResources)

            val presetBorder = resources.getDrawable(R.drawable.border, null)
            val nullBorder = resources.getDrawable(R.drawable.border_null, null)

            imageView.id = iconsList[i].id?.toInt() ?: 0

            imageView.setOnClickListener {
                imageView.background = presetBorder
                if (prevImgId > 0) {
                    val prevImg: ImageView = imageView.rootView.findViewById(prevImgId)
                    prevImg.background = nullBorder
                    prevImgId = imageView.id
                }
                if (prevImgId == 0) {
                    prevImgId = imageView.id
                }
                selectedIconId = imageView.id.toLong()
            }
            parentLayout.addView(imageView, param)
        }
    }
}