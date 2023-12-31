package whenweekly.database.repository

import whenweekly.database.DatabaseManagerImpl
import whenweekly.database.entities.Event
import whenweekly.database.entities.User
import whenweekly.domain.manager.DatabaseManager
import whenweekly.domain.repository.EventRepository
import whenweekly.misc.genInvCode
import java.time.LocalDateTime
import java.util.*

/**
 * Event database repository for handling event related operations
 *
 */
class EventDBRepository : EventRepository {
    /*
     * See database implementation for documentation
     */

    private val database: DatabaseManager = DatabaseManagerImpl()

    override fun addEvent(event: Event, owner: User): Event? {
        event.inviteCode = genInvCode()
        event.owner = owner
        val newEvent = database.addEvent(event)
        if (newEvent != null) {
            // Add owner to event
            addUserToEvent(newEvent.id, owner.id)

            // Get full owner info
            database.getUserById(owner.id)?.let {
                newEvent.owner = it
            }
        }
        return newEvent
    }

    override fun getEventByInviteCode(inviteCode: String): Event? {
        return database.getEventByInviteCode(inviteCode)
    }

    override fun getEventById(id: Int): Event? {
        return database.getEventById(id)
    }

    override fun getAllEvents(): List<Event> {
        return database.getAllEvents()
    }

    override fun addUserToEvent(eventId: Int, userId: Int): Boolean {
        // Add (empty) available dates for user
        if (!database.addAvailableDates(eventId, userId)) {
            return false
        }
        return database.addUserToEvent(eventId, userId)
    }

    override fun getEventsByUserId(userId: Int): List<Event> {
        return database.getEventsByUserId(userId)
    }

    override fun deleteEventByID(eventId: Int): Boolean {
        return database.deleteEventByID(eventId)
    }

    override fun removeUserFromEvent(eventId: Int, kickedUserID: Int): Boolean {
        if (!database.removeAvailableDates(eventId, kickedUserID)) {
            return false
        }
        return database.removeUserFromEvent(eventId, kickedUserID)
    }

    override fun getAvailableDatesByEventId(eventId: Int): List<LocalDateTime> {
        return database.getAvailableDatesByEventId(eventId)
    }

    override fun updateAvailableDates(eventId: Int, userId: Int, dates: List<LocalDateTime>): Boolean {
        return database.updateAvailableDates(eventId, userId, dates)
    }
}
