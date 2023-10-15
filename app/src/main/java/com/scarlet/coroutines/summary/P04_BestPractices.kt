package com.scarlet.coroutines.summary

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * **Understand that suspending functions await completion of their children**
 *
 * A parent coroutine cannot complete before its children, and coroutine scope functions,
 * like `coroutineScope` or `withContext`, suspend their parent until their coroutines
 * are completed. As a result, they await all the coroutines they’ve started.
 */

suspend fun longTask() = coroutineScope {
    launch {
        delay(1000)
        println("Done 1")
    }
    launch {
        delay(2000)
        println("Done 2")
    }
}

suspend fun main() {
    println("Before")
    longTask()
    println("After")
}
// Before
// (1 sec)
// Done 1
// (1 sec)
// Done 2
// After
