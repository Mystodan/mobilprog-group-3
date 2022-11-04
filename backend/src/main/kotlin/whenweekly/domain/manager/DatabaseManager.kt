package whenweekly.domain.manager

import whenweekly.domain.models.*

interface DatabaseManager {
    fun addUser(user: User): User
    fun getAllUsers(): List<User>

    fun addEvent(event: Event): Event
}
