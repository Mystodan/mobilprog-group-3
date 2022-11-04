package whenweekly.database.entities

import org.ktorm.entity.Entity

interface Event : Entity<Event> {
    val id: Int
    val name: String
    val description: String
    val owner: User
}
