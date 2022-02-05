package discork.websocket

import io.ktor.client.features.websocket.DefaultClientWebSocketSession
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class DiscordWebSocketSessionListener(
    private val session: DefaultClientWebSocketSession,
    private val heartbeatController: HeartbeatController = HeartbeatController()
) {
    suspend fun startListener() {
        coroutineScope {
            launch { session.handleIncomingMessages() }
        }
    }

    private suspend fun DefaultClientWebSocketSession.handleIncomingMessages() {
        try {
            for (message in incoming) {
                message as? Frame.Text ?: continue
                val payload = readMessageIntoPayload(message)
                launch { handlePayloadByOpcode(payload) }
            }
        } catch (e: Exception) {
            println("Error while receiving: " + e.localizedMessage)
        }
    }

    private fun readMessageIntoPayload(message: Frame.Text): DiscordWebSocketPayload {
        val messageText = message.readText()
        println("Message received: $messageText")
        return Json.decodeFromString(messageText)
    }

    private suspend fun handlePayloadByOpcode(payload: DiscordWebSocketPayload) {
        when (payload.op) {
            10 -> handleHello(payload)
            11 -> acknowledgeHeartbeat()
        }
    }

    private suspend fun handleHello(payload: DiscordWebSocketPayload) {
        heartbeatController.initiateHeartbeat(payload, session)
    }

    private fun acknowledgeHeartbeat() {
        heartbeatController.acknowledgeHeartbeat()
    }
}
