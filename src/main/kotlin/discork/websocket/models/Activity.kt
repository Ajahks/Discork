package discork.websocket.models

data class Activity(
    val name: String,
    val type: Int,
    val url: String? = null,
    val created_at: Int,
    val timestamps: Timestamps,
    val application_id: String,
    val details: String? = null,
    val state: String? = null,
    val emoji: Emoji? = null,
    val party: Party? = null,
    val assets: Assets? = null,
    val secrets: Secrets? = null,
    val instance: Boolean? = null,
    val flags: Int? = null,
    val buttons: List<Button>? = null
)

data class Timestamps(
    val start: Int? = null,
    val end: Int? = null
)

data class Emoji(
    val name: String,
    val id: String? = null,
    val animated: Boolean?
)

data class Party(
    val id: String? = null,
    val size: List<Int>
)

data class Assets(
    val large_image: String? = null,
    val large_text: String? = null,
    val small_image: String? = null,
    val small_text: String? = null
)

data class Secrets(
    val join: String? = null,
    val spectate: String? = null,
    val match: String? = null
)

data class Button(
    val label: String,
    val url: String
)