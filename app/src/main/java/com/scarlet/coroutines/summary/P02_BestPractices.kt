package com.scarlet.coroutines.summary

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

/**
 * **Suspending functions should be safe to call from any thread**
 *
 * When you call a suspending function, you shouldn’t be worried that it
 * might block the thread you’re currently using. This is especially important
 * on Android, where we often use `Dispatchers.Main`.
 */

interface SaveRepository {
    suspend fun loadSave(name: String): SaveData
}

interface DiscReader {
    suspend fun read(path: String): SaveData
}

data class SaveData(val data: Any?)

// Remember that you need to inject a dispatcher so it can be overridden for unit testing.
class DiscSaveRepository(
    private val discReader: DiscReader,
    private val dispatcher: CoroutineContext = Dispatchers.IO
) : SaveRepository {

    override suspend fun loadSave(name: String): SaveData =
        withContext(dispatcher) {
            discReader.read("save/$name")
        }
}