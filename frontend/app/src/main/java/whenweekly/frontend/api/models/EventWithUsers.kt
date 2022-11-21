package whenweekly.frontend.api.models

import kotlinx.serialization.Serializable


@Serializable
data class EventWithUsers(
    val event: Event,
    val users: List<User>
)
