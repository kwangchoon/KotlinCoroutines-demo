package com.scarlet.coroutines.summary

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob

/**
 * **Use SupervisorJob when creating CoroutineScope**
 */

// Don't
val scope1 = CoroutineScope(Job())

// Do
val scope2 = CoroutineScope(SupervisorJob())