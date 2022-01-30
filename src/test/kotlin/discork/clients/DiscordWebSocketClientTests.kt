package discork.clients

import discork.HttpRoutes
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondOk
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.http.Url
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

private const val TEST_URL = "test.url"
private const val WEBSOCKET_URL = "wss://$TEST_URL"

class DiscordWebSocketClientTests {

    private val mockEngine = MockEngine { request ->
        if (request.url == Url(HttpRoutes.GATEWAY)) {
            respond(
                content = ByteReadChannel("""{"url":"$WEBSOCKET_URL"}"""),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        } else {
            Assertions.assertEquals(TEST_URL, request.url.host)
            Assertions.assertEquals(URLProtocol.WSS, request.url.protocol)
            respondOk()
        }
    }

    private val discordWebSocketClient: DiscordWebsocketClient = DiscordWebsocketClient(mockEngine)

    @Test
    fun `given mock client when connectToGateway is called, verify that the correct gateway url is called`() {
        // Mock engine won't be able to connect to the websocket, so we will just assert parameters and expect exception
        Assertions.assertThrows(NoTransformationFoundException::class.java) {
            runBlocking {
                discordWebSocketClient.connectToGateway()
            }
        }

        discordWebSocketClient.close()
    }
}