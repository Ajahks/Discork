package discork.clients

import discork.HttpRoutes
import discork.websocket.DiscordWebSocketSessionListener
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.webSocketSession
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.http.URLProtocol
import kotlinx.serialization.Serializable

@Serializable
data class DiscordGatewayResponse(
    val url: String
)

class DiscordWebsocketClient(engine: HttpClientEngine): DiscorkHttpClient {
    override val httpClient =  HttpClient(engine) {
        install(WebSockets)
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }

    suspend fun connectToGateway() {
        val gatewayUrl = getGateway()
        val session = httpClient.webSocketSession(
            block = {
                url (gatewayUrl)
                parameter("v", 9)
                parameter("encoding", "json")
            }
        )

        // TODO: handle websocket session
    }

    override fun close() {
        httpClient.close()
    }

    private suspend fun getGateway(): String {
        val response: DiscordGatewayResponse = httpClient.get { url(HttpRoutes.GATEWAY) }
        println("Gateway found: ${response.url}")
        return response.url
    }
}