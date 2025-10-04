package com.habitrpg.android.habitica.helpers

import android.content.Context
import android.content.SharedPreferences
import androidx.core.os.bundleOf
import com.habitrpg.android.habitica.BuildConfig
import com.habitrpg.android.habitica.R

enum class AnalyticsTarget {
    AMPLITUDE,
    FIREBASE
}

enum class EventCategory(val key: String) {
    BEHAVIOUR("behaviour"),
    NAVIGATION("navigation")
}

enum class HitType(val key: String) {
    EVENT("event"),
    PAGEVIEW("pageview"),
    CREATE_WIDGET("create"),
    REMOVE_WIDGET("remove"),
    UPDATE_WIDGET("update")
}

object Analytics {
    @JvmOverloads
    fun sendEvent(
        eventAction: String?,
        category: EventCategory?,
        hitType: HitType?,
        additionalData: Map<String, Any>? = null,
        target: AnalyticsTarget? = null
    ) {
        // not doing anything for the self-hosted version
    }

    fun sendNavigationEvent(page: String) {
        val additionalData = HashMap<String, Any>()
        additionalData["page"] = page
        sendEvent("navigated $page", EventCategory.NAVIGATION, HitType.PAGEVIEW, additionalData)
    }

    fun initialize(context: Context) {
        // not doing anything for the self-hosted version
    }

    fun identify(sharedPrefs: SharedPreferences) {
        // not doing anything for the self-hosted version
    }

    fun setUserID(userID: String) {
        // not doing anything for the self-hosted version
    }

    fun setUserProperty(
        identifier: String,
        value: Any?
    ) {
        // not doing anything for the self-hosted version
    }

    fun logError(msg: String) {
        // not doing anything for the self-hosted version
    }

    fun logException(t: Throwable) {
        // not doing anything for the self-hosted version
    }

    fun setAnalyticsConsent(consents: Boolean?) {
        // not doing anything for the self-hosted version
    }


    private fun executeLambda(analyticsTarget: AnalyticsTarget, action: () -> Unit) {
        action()
    }
}
