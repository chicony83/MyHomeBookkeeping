package com.chico.myhomebookkeeping.interfaces

import com.chico.myhomebookkeeping.db.entity.IconsResource

interface OnSelectIconCallBack {
    fun selectIcon(icon:IconsResource)
}