package discork.websocket.models

import kotlinx.serialization.Serializable

@Serializable
data class HeartbeatOutgoingPayload(
    val op: Int,
    val d: Int?
)

@Serializable
data class HeartbeatHelloPayload(
    val heartbeat_interval: Long
)
