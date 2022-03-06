package discork.websocket.models

import kotlinx.serialization.json.JsonObject

data class IdentifyData(
    val token: String,
    val properties: JsonObject,
    val compress: Boolean? = null,
    val large_threshold: Int? = null,
    val shard: List<Int>? = null,
    val presence: UpdatePresence?
)

data class UpdatePresence(
    val since: Int? = null,
    val activities: List<Activity>,
    val status: String,
    val afk: Boolean
)
