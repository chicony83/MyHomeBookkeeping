package com.chico.myhomebookkeeping.interfaces.reports.dialogs

import com.chico.myhomebookkeeping.db.FullMoneyMoving

interface ListFullMoneyMovingToDialog {
    fun move(listFullMoneyMoving: List<FullMoneyMoving>)
}