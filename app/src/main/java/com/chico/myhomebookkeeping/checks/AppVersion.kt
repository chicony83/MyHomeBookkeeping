package com.chico.myhomebookkeeping.checks

import android.content.Context
import androidx.core.content.pm.PackageInfoCompat

object AppVersion {
    fun code(context: Context): Int {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        return PackageInfoCompat.getLongVersionCode(packageInfo).coerceAtMost(Int.MAX_VALUE.toLong())
            .toInt()
    }
}
