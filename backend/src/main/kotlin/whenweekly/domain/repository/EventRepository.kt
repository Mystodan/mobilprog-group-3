package whenweekly.domain.repository

import whenweekly.database.entities.Event
import whenweekly.database.entities.User
import java.time.LocalDateTime


/**
 * Event repository for handling event related operations
 *
 */
interface EventRepository {
    fun addEvent(event: Event, owner: User): Event?
    fun getEventByInviteCode(inviteCode: String): Event?
    fun getEventById(id: Int): Event?
    fun getAllEvents(): List<Event>
    fun addUserToEvent(eventId: Int, userId: Int): Boolean
    fun getEventsByUserId(userId: Int): List<Event>
    fun deleteEventByID(eventId: Int): Boolean
    fun removeUserFromEvent(eventId: Int, kickedUserID: Int): Boolean
    fun getAvailableDatesByEventId(eventId: Int): List<LocalDateTime>
    fun updateAvailableDates(eventId: Int, userId: Int, dates: List<LocalDateTime>): Boolean
}
