package whenweekly.domain.manager

import whenweekly.database.entities.Event
import whenweekly.database.entities.User

interface DatabaseManager {
    fun addUser(user: User): User
    fun getAllUsers(): List<User>

    fun getUserById(id: Int): User?

    fun addEvent(event: Event): Event
}
