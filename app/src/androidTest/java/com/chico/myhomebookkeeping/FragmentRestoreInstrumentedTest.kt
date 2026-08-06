package com.chico.myhomebookkeeping

import androidx.fragment.app.Fragment
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.chico.myhomebookkeeping.ui.cashAccount.dialogs.ChangeCashAccountDialog
import com.chico.myhomebookkeeping.ui.cashAccount.dialogs.NewCashAccountDialog
import com.chico.myhomebookkeeping.ui.cashAccount.dialogs.SelectCashAccountDialog
import com.chico.myhomebookkeeping.ui.categories.dialogs.category.ChangeCategoryDialog
import com.chico.myhomebookkeeping.ui.categories.dialogs.category.NewCategoryDialog
import com.chico.myhomebookkeeping.ui.categories.dialogs.category.SelectCategoryDialog
import com.chico.myhomebookkeeping.ui.categories.dialogs.parentCategory.NewParentCategoryDialog
import com.chico.myhomebookkeeping.ui.bottomSheet.EntryIsAddedBottomSheet
import com.chico.myhomebookkeeping.ui.calc.CalcDialogFragment
import com.chico.myhomebookkeeping.ui.currencies.dialogs.ChangeCurrencyDialog
import com.chico.myhomebookkeeping.ui.currencies.dialogs.NewCurrencyDialog
import com.chico.myhomebookkeeping.ui.currencies.dialogs.SelectCurrencyDialog
import com.chico.myhomebookkeeping.ui.dialogs.SelectIconDialog
import com.chico.myhomebookkeeping.ui.dialogs.SubmitDeleteDialog
import com.chico.myhomebookkeeping.ui.dialogs.WhatNewInLastVersionDialog
import com.chico.myhomebookkeeping.ui.dialogs.selectAsDefault.currency.SelectCurrencyAsDefaultDialog
import com.chico.myhomebookkeeping.ui.fastPaymentsPackage.dialogs.SelectRatingDialog
import com.chico.myhomebookkeeping.ui.fastPaymentsPackage.fastPayments.dialogs.SelectPaymentDialog
import com.chico.myhomebookkeeping.ui.paymentPackage.moneyMoving.dialogs.SelectMoneyMovingDialog
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.reflect.KClass

@RunWith(AndroidJUnit4::class)
class FragmentRestoreInstrumentedTest {
    @Test
    fun fragmentManagerCanInstantiateDialogsForRestore() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val fragmentClasses = listOf<KClass<out Fragment>>(
            EntryIsAddedBottomSheet::class,
            CalcDialogFragment::class,
            ChangeCashAccountDialog::class,
            NewCashAccountDialog::class,
            SelectCashAccountDialog::class,
            ChangeCategoryDialog::class,
            NewCategoryDialog::class,
            SelectCategoryDialog::class,
            NewParentCategoryDialog::class,
            ChangeCurrencyDialog::class,
            NewCurrencyDialog::class,
            SelectCurrencyDialog::class,
            SelectIconDialog::class,
            SelectRatingDialog::class,
            SelectCurrencyAsDefaultDialog::class,
            SubmitDeleteDialog::class,
            WhatNewInLastVersionDialog::class,
            SelectPaymentDialog::class,
            SelectMoneyMovingDialog::class
        )

        fragmentClasses.forEach { fragmentClass ->
            val fragment = Fragment.instantiate(
                context,
                fragmentClass.java.name
            )

            assertNotNull(fragment)
        }
    }
}
