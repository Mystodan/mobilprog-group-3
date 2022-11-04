package whenweekly.domain.manager

import whenweekly.database.entities.*

interface DatabaseManager {
    fun addUser(user: User): User
    fun getAllUsers(): List<User>

    fun getUserById(id: Int): User?

    fun addEvent(event: Event): Event
}
