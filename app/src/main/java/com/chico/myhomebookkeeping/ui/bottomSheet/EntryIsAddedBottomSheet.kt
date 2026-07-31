package com.chico.myhomebookkeeping.ui.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.interfaces.moneyMoving.OnNextEntryButtonClickedCallBack
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EntryIsAddedBottomSheet(
    private val onNextEntryButtonClickedCallBack: OnNextEntryButtonClickedCallBack
) : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val layout = inflater.inflate(R.layout.bottom_sheet_new_entry_is_added, container, false)
        val nextEntryButton = layout.findViewById<Button>(R.id.next_entry_button)

        nextEntryButton.setOnClickListener {
            onNextEntryButtonClickedCallBack.onClick()
        }

        return layout

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val initialBottomPadding = view.paddingBottom
        // Preserve the layout padding and add only the system navigation inset on edge-to-edge devices.
        ViewCompat.setOnApplyWindowInsetsListener(view) { bottomSheetView, insets ->
            val bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            bottomSheetView.updatePadding(bottom = initialBottomPadding + bottomInset)
            insets
        }
    }

}
