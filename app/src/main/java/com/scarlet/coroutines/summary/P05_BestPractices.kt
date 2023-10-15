package com.scarlet.coroutines.summary

import com.scarlet.util.onCompletion
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext

/**
 * **Understand that Job is not inherited: it is used as a parent**
 *
 * Using `withContext(SupervisorJob() or Job())` is pointless and should be considered a mistake.
 */

// Don't
fun main() = runBlocking<Unit>(SupervisorJob()) {
    coroutineContext.job.onCompletion("runBlocking")

    launch {
        delay(1000)
        throw Error()
    }
    launch {
        delay(2000)
        println("Done")
    }
    launch {
        delay(3000)
        println("Done")
    }
}
// (1 sec)
// Error...

/**/

data class Notification(val message: String)

// Don't
suspend fun sendNotifications_Bad(
    notifications: List<Notification>
) = withContext(SupervisorJob()) {
    for (notification in notifications) {
        launch {
            // Process each notification
        }
    }
}

// Do
suspend fun sendNotifications_Good(
    notifications: List<Notification>
) = supervisorScope {
    for (notification in notifications) {
        launch {
            // Process each notification
        }
    }
}