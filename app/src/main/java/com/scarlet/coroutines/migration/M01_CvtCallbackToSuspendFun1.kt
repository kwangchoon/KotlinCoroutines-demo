package com.scarlet.coroutines.migration

import com.scarlet.util.log
import kotlinx.coroutines.runBlocking
import java.io.IOException

// Callback
private interface AsyncCallback {
    fun onSuccess(result: String)
    fun onError(ex: Exception)
}

object UsingCallback_Demo1 {

    // Method using callback to simulate a long running task
    private fun getData(callback: AsyncCallback, status: Boolean = true) {
        // Do network request here, and then respond accordingly
        if (status) {
            callback.onSuccess("[Beep.Boop.Beep]")
        } else {
            callback.onError(IOException("Network failure"))
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        getData(object : AsyncCallback {
            override fun onSuccess(result: String) {
                log("Data received: $result")
            }

            override fun onError(ex: Exception) {
                log("Caught ${ex.javaClass.simpleName}")
            }
        }, true) // for success case

        getData(object : AsyncCallback {
            override fun onSuccess(result: String) {
                log("Data received: $result")
            }

            override fun onError(ex: Exception) {
                log("Caught ${ex.javaClass.simpleName}")
            }
        }, false) // for error case
    }
}

object CvtToSuspendingFunction_Demo1 {

    // Use resume/resumeWithException or resumeWith only
    private suspend fun getData(status: Boolean = true): String = TODO()

    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {

        // for success case
        getData(true).also {
            log("Data received: $it")
        }

        // for error case
        try {
            getData(false)
        } catch (ex: Exception) {
            log("Caught ${ex.javaClass.simpleName}")
        }
    }
}