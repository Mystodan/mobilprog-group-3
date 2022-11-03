package whenweekly.models

import kotlinx.serialization.Serializable;

@Serializable
data class Lobby(val id: String, val name: String, val description: String, val ownerId: String, val members: List<String>)

val lobbies = mutableListOf<Lobby>()
