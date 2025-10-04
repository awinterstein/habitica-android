package com.habitrpg.android.habitica.helpers

object CrashReporter {
    fun setCustomKey(key: String, value: String) {
        // not doing anything for the self-hosted version
    }
    fun recordException(throwable: Throwable) {
        // not doing anything for the self-hosted version
    }
}
