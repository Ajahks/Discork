import discork.clients.DiscordWebsocketClient
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        val webSocketClient = DiscordWebsocketClient(CIO.create())
        webSocketClient.connectToGateway()
        webSocketClient.close()
    }
}