package com.scarlet.coroutines.summary

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * **Avoid using Job builder, except for constructing a scope**
 *
 * When you create a job using the `Job` function, it is created in the
 * active state regardless of the state of its children. Even if some children
 * have completed, this doesn’t mean their parents have also completed.
 */

suspend fun main(): Unit = coroutineScope {
    val job = Job()
    launch(job) {
        delay(1000)
        println("Text 1")
    }
    launch(job) {
        delay(2000)
        println("Text 2")
    }
    job.join() // Here we will await forever
    println("Will not be printed")
}
// (1 sec)
// Text 1
// (1 sec)
// Text 2
// (runs forever)

/**
 * Good Practices
 */

class SomeService1 {
    private var job: Job? = null
    private val scope = CoroutineScope(SupervisorJob())

    // Every time we start a new task,
    // we cancel the previous one.
    fun startTask() {
        cancelTask()
        job = scope.launch {
            // ...
        }
    }

    fun cancelTask() {
        job?.cancel()
    }
}

class SomeService2 {
    private var jobs: List<Job> = emptyList()
    private val scope = CoroutineScope(SupervisorJob())

    fun startTask() {
        jobs += scope.launch {
            // ...
        }
    }

    fun cancelTask() {
        jobs.forEach { it.cancel() }
    }
}