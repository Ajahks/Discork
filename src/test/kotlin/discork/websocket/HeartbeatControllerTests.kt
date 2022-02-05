package discork.websocket

import discork.websocket.models.HeartbeatOutgoingPayload
import io.ktor.client.features.websocket.DefaultClientWebSocketSession
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

private const val HEARTBEAT_INTERVAL = 50L
private const val SEQUENCE_NUMBER = 123

@ExtendWith(MockKExtension::class)
class HeartbeatControllerTests{

    private val outgoingHeartbeat = HeartbeatOutgoingPayload(op = 1, d = null)
    private val outgoingHeartbeatWithSequence = HeartbeatOutgoingPayload(op = 1, d = SEQUENCE_NUMBER)

    private var sentAcknowledge = false

    @RelaxedMockK
    private lateinit var mockSession: DefaultClientWebSocketSession

    private val heartbeatController = HeartbeatController()

    @BeforeEach
    fun setup() {
        sentAcknowledge = false
    }

    @Test
    fun `given heartbeat initiated with valid payload, verify that the right frame is sent to the discord socket`() {
        val payload = getTestPayload()
        val expectedFrame = Frame.Text(Json.encodeToString(outgoingHeartbeat))

        runBlocking{
            heartbeatController.initiateHeartbeat(payload, mockSession)
        }

        coVerify {
            mockSession.send(
                match { frame ->
                    frame is Frame.Text && frame.readText() == expectedFrame.readText()
                }
            )
        }
    }

    @Test
    fun `given heartbeat initiated with valid payload and acknowledged frame, verify that we send 3 heartbeats`() {
        val payload = getTestPayload()
        coEvery { mockSession.send(any()) } coAnswers {
            delay(HEARTBEAT_INTERVAL / 2)
            if (!sentAcknowledge) {
                heartbeatController.acknowledgeHeartbeat()
                sentAcknowledge = true
            }
        }

        runBlocking{
            heartbeatController.initiateHeartbeat(payload, mockSession)
        }

        coVerify(exactly = 3) {
            mockSession.send(any())
        }
    }

    @Test
    fun `given initiated HeartbeatController with updated sequence number, verify that heartbeat sends sequence number`() {
        val payload = getTestPayload()
        val expectedFrame = Frame.Text(Json.encodeToString(outgoingHeartbeatWithSequence))

        heartbeatController.updateSequenceNumber(SEQUENCE_NUMBER)
        runBlocking{
            heartbeatController.initiateHeartbeat(payload, mockSession)
        }

        coVerify {
            mockSession.send(
                match { frame ->
                    frame is Frame.Text && frame.readText() == expectedFrame.readText()
                }
            )
        }
    }

    private fun getTestPayload(): DiscordWebSocketPayload {
        val jsonPayload = """ 
            {
                "heartbeat_interval": $HEARTBEAT_INTERVAL
            }
        """.trimIndent()
        return DiscordWebSocketPayload(
            op = 11,
            d = Json.decodeFromString(jsonPayload)
        )
    }
}