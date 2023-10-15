package com.scarlet.coroutines.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * **Before using a scope, consider under which conditions it is cancelled**
 *
 * One of heuristics for using Kotlin Coroutines on Android is "choosing what
 * scope you should use is choosing when you want this coroutine cancelled".
 */

@DelicateCoroutinesApi
class MainViewModel : ViewModel() {
    val scope = CoroutineScope(SupervisorJob())

    fun onCreate() {
        viewModelScope.launch {
            // Will be cancelled with MainViewModel
            launch { task1() }
            // Will never be cancelled
            GlobalScope.launch { task2() }
            // Will be cancelled when we cancel scope
            scope.launch { task2() }
        }
    }
}

suspend fun task1() = delay(1_000)
suspend fun task2() = delay(1_000)
