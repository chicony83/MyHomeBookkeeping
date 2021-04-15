package com.chico.myhomebookkeeping.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

fun launchIo(task: suspend () -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        task()
    }
}

fun launchUi(task: suspend () -> Unit) {
    CoroutineScope(Dispatchers.Main).async {
        task()
    }
}

suspend fun <R> launchForResult(task: suspend () -> R): R? {
    return CoroutineScope(Dispatchers.IO).async {
        task()
    }.await()
}