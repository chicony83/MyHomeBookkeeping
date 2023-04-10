package com.chico.myhomebookkeeping.interfaces

interface OnItemViewClickListenerLong {
    fun onClick(selectedId: Long)

}

interface OnItemViewClickListener {
    fun onShortClick(selectedId: Int)
    fun onLongClick(selectedId: Int)
}