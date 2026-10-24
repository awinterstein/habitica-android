package com.habitrpg.android.habitica.helpers

import android.util.Base64
import com.habitrpg.common.habitica.models.auth.UserAuthResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.security.MessageDigest
import java.security.SecureRandom

/**
 * Login through the OpenID Connect provider of the server, as a native app should (RFC 8252):
 * the browser handles the provider, the server returns a short-lived code through habitica://oidc
 * and the app redeems it with the PKCE verifier only it knows, so an intercepted link is worthless.
 */
object OidcClient {
    private val http = OkHttpClient()

    fun randomToken(): String = ByteArray(32).also { SecureRandom().nextBytes(it) }.base64Url()

    fun codeChallenge(verifier: String): String =
        MessageDigest.getInstance("SHA-256").digest(verifier.toByteArray()).base64Url()

    private fun ByteArray.base64Url(): String =
        Base64.encodeToString(this, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)

    /** Name of the provider, null when the server does not offer the login */
    suspend fun providerName(server: String): String? = withContext(Dispatchers.IO) {
        val request = Request.Builder().url("${server.trimEnd('/')}/api/v4/user/auth/oidc/config").build()
        http.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected response ${response.code}")
            JSONObject(response.body.string()).getJSONObject("data").optString("name").ifEmpty { null }
        }
    }

    suspend fun redeemCode(server: String, code: String, verifier: String): UserAuthResponse = withContext(Dispatchers.IO) {
        val body = JSONObject().put("code", code).put("code_verifier", verifier).toString()
        val request = Request.Builder()
            .url("${server.trimEnd('/')}/api/v4/user/auth/oidc/token")
            .post(body.toRequestBody("application/json".toMediaType()))
            .build()
        http.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Login failed with ${response.code}")
            val data = JSONObject(response.body.string()).getJSONObject("data")
            UserAuthResponse(apiToken = data.getString("apiToken"), id = data.getString("id"), newUser = data.optBoolean("newUser"))
        }
    }
}
