package com.scarlet.coroutines.cancellation

import com.scarlet.util.log
import kotlinx.coroutines.*

object Uncooperative_Cancellation {

    private suspend fun printTwice() = withContext(Dispatchers.Default) {
        val startTime = System.currentTimeMillis()
        var nextPrintTime = startTime
        while (true) {
            if (System.currentTimeMillis() >= nextPrintTime) {
                log("I'm working..")
                nextPrintTime += 500
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val job = launch {
            printTwice()
        }

        delay(1_500)
        log("Cancelling job ...")
        job.cancelAndJoin()
    }
}

/**
 * CoroutineScope.{isActive, ensureActive()} and delay()
 *
 * Think about how to handle cleanup?
 */
object Cooperative_Cancellation {

    // How to make sure this suspending function be cooperative?
    private suspend fun printTwice() = withContext(Dispatchers.Default) {
        val startTime = System.currentTimeMillis()
        var nextPrintTime = startTime
        while (true) {
            if (System.currentTimeMillis() >= nextPrintTime) {
                println("I'm working..")
                nextPrintTime += 500
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val job = launch {
            printTwice()
        }

        delay(1500)
        log("Cancelling job ...")
        job.cancelAndJoin()
    }
}

object Cleanup_When_Cancelled {

    private suspend fun printTwice() = withContext(Dispatchers.Default) {
        val startTime = System.currentTimeMillis()
        var nextPrintTime = startTime
        while (isActive) {
            if (System.currentTimeMillis() >= nextPrintTime) {
                log("job: I'm working..")
                nextPrintTime += 500
            }
        }

        // TODO: cleanup
        log("job: I'm cancelled")
        cleanUp()
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val job = launch {
            printTwice()
        }

        delay(1_500)

        log("Try to cancel the job ...")
        job.cancelAndJoin()
    }

    private suspend fun cleanUp() {
        delay(100)
        log("Cleanup ...")
    }
}