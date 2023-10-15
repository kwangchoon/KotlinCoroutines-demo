package com.scarlet.coroutines.exceptions

import com.scarlet.util.log
import com.scarlet.util.onCompletion
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.lang.RuntimeException

@ExperimentalCoroutinesApi
class CoroutineScopeBuilderTest {

    /**
     * **coroutineScope** has a `Job()`.
     * A failing child causes the cancellation of its parent and siblings.
     * Does not propagate exception, just rethrows it!!
     */

    @Test
    fun `coroutineScope rethrows exception`() = runTest {
        try {
            // rethrows uncaught exception
            coroutineScope {
                onCompletion("coroutineScope")

                launch {
                    delay(100)
                    throw RuntimeException("oops")
                }.onCompletion("child")
            }

        } catch (ex: Exception) {
            log("Caught: $ex")
        }
    }

    @Test
    fun `coroutineScope - cancelling the scope cancels itself and all its children`() = runTest {
        try {
            // rethrows uncaught exception
            coroutineScope {
                onCompletion("coroutineScope")

                launch { delay(500) }.onCompletion("child1")
                launch { delay(500) }.onCompletion("child2")

                delay(100)

                coroutineContext.cancel()
//                coroutineContext.cancelChildren()
            }
        } catch (ex: Exception) {
            log("Caught: $ex")
        }
    }

    @Test
    fun `coroutineScope - failing child causes cancellation of its parent and sibling`() = runTest {
        try {
            // rethrows uncaught exception
            coroutineScope {
                onCompletion("coroutineScope")

                launch {
                    delay(500)
                    throw RuntimeException("oops")
                }.onCompletion("child1")

                launch {
                    delay(1_000)
                }.onCompletion("child2")
            }
        } catch (ex: Exception) {
            log("Caught: $ex")
        }
    }

    @Test
    fun `coroutineScope as a sub-scope of other coroutine`() = runTest {
        val scope = CoroutineScope(Job())

        val parentJob = scope.launch {
            try {
                coroutineScope {
                    onCompletion("coroutineScope")

                    launch {
                        delay(500)
                        throw RuntimeException("oops")
                    }.onCompletion("child1")

                    launch {
                        delay(1_000)
                    }.onCompletion("child2")
                }
            } catch (ex: Exception) {
                log("Caught: $ex")
            }
        }.onCompletion("parentJob")

        parentJob.join()
    }
}