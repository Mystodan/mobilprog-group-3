package whenweekly.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val id: Int,
    val name: String,
    val description: String,
    val owner: User
)
