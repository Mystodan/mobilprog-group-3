package whenweekly.domain.manager

import whenweekly.database.entities.Event
import whenweekly.database.entities.User
import java.time.LocalDateTime

/**
 * Database manager for handling database related operations
 *
 */
interface DatabaseManager {
    fun addUser(user: User): User?
    fun getAllUsers(): List<User>
    fun getUserById(id: Int): User?
    fun updateUser(user: User): User?
    fun addEvent(event: Event): Event?
    fun getEventByInviteCode(inviteCode: String): Event?
    fun getEventById(id: Int): Event?
    fun getAllEvents(): List<Event>
    fun addUserToEvent(eventId: Int, userId: Int): Boolean
    fun removeUserFromEvent(eventId: Int, kickedUserID: Int): Boolean
    fun getEventsByUserId(userId: Int): List<Event>
    fun getUsersByEventId(eventId: Int): List<User>
    fun getUserByUUID(uuid: String): User?
    fun deleteEventByID(eventId: Int): Boolean
    fun resetDatabase()
    fun getAvailableDatesByEventId(eventId: Int): List<LocalDateTime>
    fun addAvailableDates(eventId: Int, userId: Int): Boolean
    fun updateAvailableDates(eventId: Int, userId: Int, dates: List<LocalDateTime>): Boolean
    fun removeAvailableDates(eventId: Int, userId: Int): Boolean
}
