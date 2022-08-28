package com.chico.myhomebookkeeping.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.utils.launchIo
import kotlinx.coroutines.delay

class EntryAddedDialog:DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_entry_added,null)

            buildLayout(layout)
            builder.setView(layout)

            launchIo {
                delay(3500)
                dialog?.cancel()
            }

            builder.create()
        }?:throw IllegalStateException(getString(R.string.exceptions_activity_cant_be_null))
    }

    private fun buildLayout(layout: View?) {

    }
}