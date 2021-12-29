package com.chico.myhomebookkeeping.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.helpers.Message
import java.lang.IllegalStateException

class SubmitDeleteDialog:DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { it->
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_submit_delete,null)
            buildLayout(layout)
            builder.setView(layout)
            val submitButton = layout.findViewById<Button>(R.id.submitButton)
            val cancelButton = layout.findViewById<Button>(R.id.cancelButton)

            submitButton.setOnClickListener { Message.log("submit button pressed") }
            cancelButton.setOnClickListener { Message.log("cancel button pressed") }
            builder.create()
        }?:throw IllegalStateException(getString(R.string.exceptions_activity_cant_be_null))
    }

    private fun buildLayout(layout: View) {

    }
}