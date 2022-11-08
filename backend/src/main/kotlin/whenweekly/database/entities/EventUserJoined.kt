package whenweekly.database.entities

import org.ktorm.entity.Entity
import java.time.LocalDateTime

interface EventUserJoined : Entity<EventUserJoined> {
    val event: Event
    val user: User
    val join_time: LocalDateTime
}
