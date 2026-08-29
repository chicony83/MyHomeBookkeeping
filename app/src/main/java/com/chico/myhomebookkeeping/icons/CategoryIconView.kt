package com.chico.myhomebookkeeping.icons

import android.widget.ImageView
import com.chico.myhomebookkeeping.R

fun ImageView.setCategoryIcon(iconKey: String?) {
    val canonicalKey = CategoryIconCatalog.canonicalKey(iconKey)
    if (canonicalKey == null) {
        setImageResource(R.drawable.no_image)
    } else {
        setImageDrawable(MaterialSymbolDrawable(context, canonicalKey))
    }
}
