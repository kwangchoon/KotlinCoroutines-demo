package com.scarlet.coroutines.summary

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * **Don’t use GlobalScope**
 *
 * ```
 * public object GlobalScope : CoroutineScope {
 *     override val coroutineContext: CoroutineContext
 *         get() = EmptyCoroutineContext
 * }
 * ```
 *
 * `GlobalScope` means no relation, no cancellation, and is hard to override
 * for testing. Even if `GlobalScope` is all you need now, defining a
 * meaningful scope might be helpful in the future.
 */

val scope = CoroutineScope(SupervisorJob())

@DelicateCoroutinesApi
fun example() {
    // Don't
    GlobalScope.launch { task() }

    // Do
    scope.launch { task() }
}

suspend fun task() = delay(1_000)
