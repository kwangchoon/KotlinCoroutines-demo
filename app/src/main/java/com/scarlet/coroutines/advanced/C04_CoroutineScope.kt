@file:OptIn(DelicateCoroutinesApi::class)

package com.scarlet.coroutines.advanced

import com.scarlet.util.*
import kotlinx.coroutines.*

/**
 * When a coroutine is launched in the `CoroutineScope` of another coroutine,
 * it inherits its context via `CoroutineScope.coroutineContext` and the `Job`
 * of the new coroutine becomes a child of the parent coroutine's job.
 *
 * When the parent coroutine is cancelled, all its children are recursively cancelled,
 * too. This is a very powerful feature, because it allows you to cancel all coroutines.
 */

object CoroutineScope_Has_Context {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        val scope = CoroutineScope(Job() + CoroutineName("My Scope"))
        scopeInfo(scope, 0)

        // Dispatchers.Default
        scope.launch(CoroutineName("Top-level Coroutine")) {
            delay(100)
            coroutineInfo(1)
        }.join() // need to prevent early exit
    }
}

object Canceling_Scope_Cancels_It_and_Its_Job {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        val scope = CoroutineScope(CoroutineName("My Scope"))
        // New job gets created if not provided explicitly
        if (scope.coroutineContext[Job] != null) {
            log("New job is created!")
        }

        // Dispatchers.Default
        val job = scope.launch(CoroutineName("Top-level Coroutine")) {
            delay(1_000)
        }.onCompletion("job")

        delay(500)

        scope.cancel()
        job.join() // why need this?

        log("Done.")
    }
}

object Canceling_Scope_Cancels_It_and_Its_Job_and_All_Descendants {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        val scope = CoroutineScope(Job())

        val parent1 = scope.launch(CoroutineName("Parent 1")) {
            launch { delay(1_000); log("child 1 done") }.onCompletion("child 1")
            launch { delay(1_000); log("child 2 done") }.onCompletion("child 2")
        }.onCompletion("parent 1")

        val parent2 = scope.launch(CoroutineName("Parent 2")) {
            launch { delay(1_000); log("child 3 done") }.onCompletion("child 3")
            launch { delay(1_000); log("child 4 done") }.onCompletion("child 4")
        }.onCompletion("parent 2")

        delay(500)
        scope.cancel()

        joinAll(parent1, parent2)
        log("Done")
    }
}

object Canceling_A_Scope_Does_Not_Affect_Its_Siblings {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        val scopeLeft = CoroutineScope(Job())

        val parentLeft = scopeLeft.launch(CoroutineName("Parent Left")) {
            launch { delay(1_000); log("child L-1 done") }.onCompletion("child L-1")
            launch { delay(1_000); log("child L-2 done") }.onCompletion("child L-2")
        }.onCompletion("parent left")

        val scopeRight = CoroutineScope(Job())

        val parentRight = scopeRight.launch(CoroutineName("Parent Right")) {
            launch { delay(1_000); log("child R-1 done") }.onCompletion("child R-1")
            launch { delay(1_000); log("child R-2 done") }.onCompletion("child R-2")
        }.onCompletion("parent right")

        delay(500)
        scopeLeft.cancel()

        joinAll(parentLeft, parentRight)
        log("Done")
    }
}

object GlobalScope_Cancellation_Demo {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        log("Job for GlobalScope is ${GlobalScope.coroutineContext[Job]}")

        val job = GlobalScope.launch {
            launch(CoroutineName("Child 1")) { delay(100) }.onCompletion("Child 1")
            launch(CoroutineName("Child 2")) { delay(1_000) }.onCompletion("Child 2")
            log("GlobalScope is active")
//            delay(700)
        }.onCompletion("Parent")

        delay(500)

        log(job.children.toList().toString())

        job.cancelAndJoin()
        // what will happen? GlobalScope.cancel()
//        GlobalScope.cancel()
    }
}

object GlobalScope_Cancellation_Demo3 {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        log("Job for GlobalScope is ${GlobalScope.coroutineContext[Job]}")

        val job = GlobalScope.launch(CoroutineName("Parent")) {
            launch(CoroutineName("Child 1")) { delay(1_000) }.onCompletion("Child 1")
            launch(CoroutineName("Child 2")) { delay(1_000) }.onCompletion("Child 2")
            log("GlobalScope is active")
//            delay(700)
        }.onCompletion("Parent")

        GlobalScope.launch(CoroutineName("Parent 2")) {
//            launch(CoroutineName("Child3")) { delay(1_000)}.onCompletion("Child 3")
            delay(700)
        }.onCompletion("Parent 2")

        delay(500)

        log(job.children.toList().toString())

        job.cancelAndJoin()
        // what will happen? GlobalScope.cancel()
//        GlobalScope.cancel()

        delay(2_000)
    }
}

object GlobalScope_Cancellation_Demo4 {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        log("Job for GlobalScope is ${GlobalScope.coroutineContext[Job]}")

        val job = GlobalScope.launch(CoroutineName("Parent")) {
            launch(CoroutineName("Child 1")) {
                launch(CoroutineName("Grand Child")) {
                    delay(1_000)
                }.onCompletion("Grand Child")
                delay(1_000)
            }.onCompletion("Child 1")
            launch(CoroutineName("Child 2")) { delay(1_000) }.onCompletion("Child 2")

            log("GlobalScope is active")
//            delay(700)
        }.onCompletion("Parent")

        delay(500)

        log(job.children.toList().toString())

        job.cancelAndJoin()
        // what will happen? GlobalScope.cancel()
//        GlobalScope.cancel()
    }
}

object GlobalScope_Cancellation_Demo2 {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        val job = launch {
            launch(CoroutineName("Child 1")) { delay(1_000) }.onCompletion("Child 1")
            launch(CoroutineName("Child 2")) { delay(900) }.onCompletion("Child 2")
            delay(700)
            log("GlobalScope is active")
        }.onCompletion("Parent")

        delay(500)

        job.cancelAndJoin()
        // what will happen? GlobalScope.cancel()
//        GlobalScope.cancel()
    }
}

object GlobalScope_Cancellation_Demo5 {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        val job = launch {
            launch(CoroutineName("Child 1")) {
                launch(CoroutineName("Grand Child")) {
                    delay(1_000)
                }.onCompletion("Grand Child")
//                delay(700)
            }.onCompletion("Child 1")
            launch(CoroutineName("Child 2")) { delay(1_000) }.onCompletion("Child 2")
        }.onCompletion("Parent")

        delay(500)

        job.cancelAndJoin()
        // what will happen? GlobalScope.cancel()
//        GlobalScope.cancel()
    }
}
