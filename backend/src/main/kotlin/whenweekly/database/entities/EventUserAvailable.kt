package whenweekly.database.entities

import org.ktorm.entity.Entity

interface EventUserAvailable : Entity<EventUserAvailable> {
    val event: Event
    val user: User
    val available_dates: String
}
