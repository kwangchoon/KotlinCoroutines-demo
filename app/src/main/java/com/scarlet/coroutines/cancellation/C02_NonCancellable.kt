package com.scarlet.coroutines.cancellation

import com.scarlet.util.log
import com.scarlet.util.onCompletion
import kotlinx.coroutines.*
import java.lang.Exception

/**
 * If **Job** is already in a _Cancelling_ state, then suspension or starting
 * another coroutine is not possible at all.
 *
 * If we try to start another coroutine, it will just be _ignored_.
 *
 * If we try to suspend, it will throw `CancellationException`.
 */

object Try_Launch_Or_Call_Suspending_Function_in_Canceling_State {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        val job = launch {
            try {
                delay(200)
                log("Unreachable code") // because it will be cancelled after 100ms
            } finally {
                log("Finally")

                log("isActive = ${coroutineContext.isActive}, isCancelled = ${coroutineContext.job.isCancelled}")

                // Try to launch new coroutine
                launch { // will be ignored because of immediate cancellation
                    log("Will not be printed")
                    delay(50)
                }.onCompletion("Jombi")//.join() // will throw cancellation exception and skip the rest

                // Try to call suspending function will throw cancellation exception
                try {
                    delay(100)
                    log("Will not be printed")
                } catch (ex: Exception) {
                    log("Caught: $ex")
                }
                // Nevertheless, if you want to call suspending function to clean up ... how to do?
            }
        }

        delay(100)
        job.cancelAndJoin()
        log("Cancel done")
    }
}

object Call_Suspending_Function_in_Cancelling_State_To_Cleanup {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val job = launch {
            try {
                delay(200)
                println("Coroutine finished")
            } finally {
                println("Finally")

                // DO NOT USE NonCancellable with `launch` or `async`
                cleanUp()
                println("Cleanup done")
            }
        }

        delay(100)
        job.cancelAndJoin()
        println("Cancel done")
    }

    private suspend fun cleanUp() {
        delay(3_000)
    }
}

