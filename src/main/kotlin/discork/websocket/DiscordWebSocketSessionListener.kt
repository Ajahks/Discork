package discork.websocket

import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.WebSocketSession
import io.ktor.http.cio.websocket.close
import io.ktor.http.cio.websocket.readText
import kotlinx.coroutines.runBlocking

class DiscordWebSocketSessionListener(
    private val session: WebSocketSession
) {
    init {
        runBlocking {
            while (true) {
                handleIncomingMessage()
            }
        }
    }

    private suspend fun handleIncomingMessage() {
        val incomingMessage = session.incoming.receive() as? Frame.Text
        println(incomingMessage?.readText())

        // TODO: Handle different messages based on opcodes
        session.close()
    }
}