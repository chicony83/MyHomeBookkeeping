package com.chico.myhomebookkeeping.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.db.entity.IconsResource
import java.lang.IllegalStateException

class SelectIconDialog(iconsList: List<IconsResource>) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { it ->
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_select_icon, null)
            buildLayout(layout)
            builder.setView(layout)

            builder.create()
        } ?: throw  IllegalStateException(getString(R.string.exceptions_activity_cant_be_null))
    }

    private fun buildLayout(layout: View) {

    }
}