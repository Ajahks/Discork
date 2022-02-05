package discork.websocket

import io.ktor.client.features.websocket.DefaultClientWebSocketSession
import io.ktor.http.cio.websocket.Frame
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.channels.ChannelIterator
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class DiscordWebSocketSessionListenerTests {

    private class TestChannelIterator(
        frameList: List<Frame>
    ) : ChannelIterator<Frame> {

        private val frameListIterator = frameList.listIterator()

        override suspend fun hasNext(): Boolean {
            return frameListIterator.hasNext()
        }

        override fun next(): Frame {
            return frameListIterator.next()
        }
    }

    private val helloFrame = Frame.Text("""{"op":10}""")
    private val ackFrame = Frame.Text("""{"op":11}""")
    private val badFrame = Frame.Text("""{"op" 11{""")

    @RelaxedMockK
    private lateinit var mockHeartbeatController: HeartbeatController
    @MockK
    private lateinit var mockSession: DefaultClientWebSocketSession
    @MockK
    private lateinit var receiveChannel: ReceiveChannel<Frame>

    private lateinit var webSocketListener: DiscordWebSocketSessionListener

    @BeforeEach
    fun setup() {
        every { mockSession.incoming } returns receiveChannel
        webSocketListener = DiscordWebSocketSessionListener(mockSession, mockHeartbeatController)
    }

    @Test
    fun `given session has incoming message of opcode 10, verify that hello is handled`() {
        val frameListIterator = TestChannelIterator(listOf(helloFrame))
        every { receiveChannel.iterator() } returns frameListIterator

        runBlocking {
            webSocketListener.startListener()
        }

        coVerify(exactly = 1) { mockHeartbeatController.initiateHeartbeat(any(), any()) }
    }

    @Test
    fun `given session has incoming message of opcode 11, verify that heartbeat is acknowledged`() {
        val frameListIterator = TestChannelIterator(listOf(ackFrame))
        every { receiveChannel.iterator() } returns frameListIterator

        runBlocking {
            webSocketListener.startListener()
        }

        coVerify(exactly = 1) { mockHeartbeatController.acknowledgeHeartbeat() }
    }

    @Test
    fun `given session has incoming bad message, verify that exception is handled`() {
        val frameListIterator = TestChannelIterator(listOf(badFrame))
        every { receiveChannel.iterator() } returns frameListIterator

        runBlocking {
            webSocketListener.startListener()
        }
    }
}