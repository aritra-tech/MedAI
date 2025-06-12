package com.aritradas.medai.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


fun ViewModel.runIO(block: suspend CoroutineScope.() -> Unit) {
    viewModelScope.launch(Dispatchers.IO, block = block)
}

suspend fun <T> withIOContext(block: suspend CoroutineScope.() -> T): T {
    return withContext(Dispatchers.IO) {
        block()
    }
}

fun ViewModel.runMain(block: suspend CoroutineScope.() -> Unit) {
    viewModelScope.launch(Dispatchers.Main) {
        block()
    }
}

suspend fun <T> withMainContext(block: suspend CoroutineScope.() -> T): T {
    return withContext(Dispatchers.Main) {
        block()
    }
}

fun ViewModel.runDefault(block: suspend CoroutineScope.() -> Unit) {
    viewModelScope.launch(Dispatchers.Default) {
        block()
    }
}

suspend fun <T> withDefaultContext(block: suspend CoroutineScope.() -> T): T {
    return withContext(Dispatchers.Default) {
        block()
    }
}

fun ViewModel.runUnconfined(block: suspend CoroutineScope.() -> Unit) {
    viewModelScope.launch(Dispatchers.Unconfined) {
        block()
    }
}

suspend fun <T> withUnconfinedContext(block: suspend CoroutineScope.() -> T): T {
    return withContext(Dispatchers.Unconfined) {
        block()
    }
}