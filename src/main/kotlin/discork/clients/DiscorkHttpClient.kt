package discork.clients

import io.ktor.client.HttpClient

interface DiscorkHttpClient {
    val httpClient: HttpClient
    fun close()
}