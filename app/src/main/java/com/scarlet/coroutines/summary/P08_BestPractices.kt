package com.scarlet.coroutines.summary

import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren

/**
 * **Consider cancelling scope children**
 */

fun onCleared() {
    // Consider doing
    scope.coroutineContext.cancelChildren()

    // Instead of
    scope.cancel()
}