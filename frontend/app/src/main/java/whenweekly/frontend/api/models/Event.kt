package whenweekly.frontend.api.models
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class Event(
    val id: Int,
    val name: String,
    val description: String,
    val start_date: LocalDateTime,
    val end_date: LocalDateTime,
    val owner: User,
)