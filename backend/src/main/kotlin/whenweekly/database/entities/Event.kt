package whenweekly.database.entities

import org.ktorm.entity.Entity
import java.time.LocalDateTime

interface Event : Entity<Event> {
    val id: Int
    val name: String
    val description: String
    val start_date: LocalDateTime
    val end_date: LocalDateTime
    val owner: User
}
