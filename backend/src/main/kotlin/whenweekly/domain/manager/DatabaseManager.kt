package whenweekly.domain.manager

import whenweekly.database.entities.Event
import whenweekly.database.entities.User

interface DatabaseManager {
    fun addUser(user: User): User
    fun getAllUsers(): List<User>
    fun getUserById(id: Int): User?

    fun addEvent(event: Event): Event

    fun getEventById(id: Int): Event?

    fun getAllEvents(): List<Event>

    fun addUserToEvent(eventId: Int, userId: Int): Boolean

    fun getEventsByUserId(userId: Int): List<Event>

    fun getUserByUUID(uuid: String): User?

    fun deleteEventByID(eventId: Int)

    fun resetDatabase()
}
