package discork.websocket

import discork.websocket.models.HeartbeatOutgoingPayload
import discork.websocket.models.HeartbeatHelloPayload
import io.ktor.client.features.websocket.DefaultClientWebSocketSession
import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.close
import io.ktor.http.cio.websocket.readText
import kotlinx.coroutines.delay
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlin.random.Random

class HeartbeatController {
    private lateinit var session: DefaultClientWebSocketSession
    private var heartbeatInterval = -1L
    private var firstHeartbeatInterval = -1L
    private var waitingForAck = false
    private var lastSequenceNumber: Int? = null

    private val json = Json {
        ignoreUnknownKeys = true
    }

    suspend fun initiateHeartbeat(payload: DiscordWebSocketPayload, session: DefaultClientWebSocketSession) {
        this.session = session
        val heartbeatPayload: HeartbeatHelloPayload = json.decodeFromJsonElement(payload.d!!)
        val jitter = Random(System.currentTimeMillis()).nextDouble()
        heartbeatInterval = heartbeatPayload.heartbeat_interval
        firstHeartbeatInterval = (heartbeatInterval * jitter).toLong()
        sendFirstHeartbeat()
    }

    fun acknowledgeHeartbeat() {
        println("Heartbeat acknowledged")
        waitingForAck = false
    }

    fun updateSequenceNumber(sequenceNumber: Int) {
        lastSequenceNumber = sequenceNumber
    }

    private suspend fun sendFirstHeartbeat() {
        delay(firstHeartbeatInterval)
        sendHeartbeat()
        runHeartbeatLoop()
    }

    private suspend fun runHeartbeatLoop() {
        while(true) {
            delay(heartbeatInterval)
            if (!waitingForAck) {
                sendHeartbeat()
            } else {
                println("Last heartbeat was not acknowledged!")
                break
            }
        }

        println("Closing connection")
        session.close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, "Last heartbeat was not acknowledged"))
    }

    private suspend fun sendHeartbeat() {
        val heartbeat = HeartbeatOutgoingPayload(op = 1, d=lastSequenceNumber)
        val frame = Frame.Text(Json.encodeToString(heartbeat))
        println("Sending heartbeat to discord: ${frame.readText()}")
        waitingForAck = true
        session.send(frame)
    }
}