package discordApi

import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response

class OAuthHandler(
    private val client: HttpHandler
) {
    fun exchangeCodeForToken(
        clientId: String,
        clientSecret: String,
        code: String
    ): Response {
        val request = Request(Method.POST, "https://discord.com/api/v9")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .query("client_id", clientId)
            .query("client_secret", clientSecret)
            .query("grant_type", "authorization_code")
            .query("code", code)

        return client(request)
    }

    fun requestNewToken(
        clientId: String,
        clientSecret: String,
        refreshToken: String
    ): Response {
        val request = Request(Method.POST, "https://discord.com/api/v9")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .query("client_id", clientId)
            .query("client_secret", clientSecret)
            .query("grant_type", "refresh_token")
            .query("refresh_token", refreshToken)

        return client(request)
    }
}