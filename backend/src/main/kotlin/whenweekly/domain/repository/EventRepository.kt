package whenweekly.domain.repository

import whenweekly.database.entities.Event
import whenweekly.database.entities.User


interface EventRepository {
    fun addEvent(event: Event, owner: User): Event?

    fun getEventByInviteCode(inviteCode: String): Event?
    fun getEventById(id: Int): Event?

    fun getAllEvents(): List<Event>

    fun addUserToEvent(eventId: Int, userId: Int): Boolean

    fun getEventsByUserId(userId: Int): List<Event>

    fun deleteEventByID(id: Int): Boolean

    fun removeUserFromEvent(eventId: Int,kickedUserID: Int):Boolean
}
