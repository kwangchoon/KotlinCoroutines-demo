package com.scarlet.coroutines.summary

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

/**
 * **Remember to use yield in heavy functions**
 *
 * It is good practice to use yield in suspending functions between blocks of
 * non-suspended CPU-intensive or time-intensive operations. This function
 * suspends and immediately resumes the coroutine, thus it supports cancellation.
 *
 * Inside coroutine builders, you can also use `ensureActive`.
 */

suspend fun cpuIntensiveOperations() =
    withContext(Dispatchers.Default) {
        cpuIntensiveOperation1()
        yield()
        cpuIntensiveOperation2()
        yield()
        cpuIntensiveOperation3()
    }

fun cpuIntensiveOperation1() {
    TODO("Not yet implemented")
}

fun cpuIntensiveOperation2() {
    TODO("Not yet implemented")
}

fun cpuIntensiveOperation3() {
    TODO("Not yet implemented")
}
