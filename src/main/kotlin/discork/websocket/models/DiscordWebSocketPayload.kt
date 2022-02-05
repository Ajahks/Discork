package discork.websocket

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class DiscordWebSocketPayload(
    val op: Int,
    val t: String? = null,
    val s: Int? = null,
    val d: JsonObject? = null
)
