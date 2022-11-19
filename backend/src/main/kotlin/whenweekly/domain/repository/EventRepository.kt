package whenweekly.domain.repository

import whenweekly.database.entities.Event


interface EventRepository {
    fun addEvent(event: Event): Event

    fun getEventById(id: Int): Event?

    fun getAllEvents(): List<Event>

    fun addUserToEvent(eventId: Int, userId: Int): Boolean

    fun getEventsByUserId(userId: Int): List<Event>

    fun deleteEventByID(id: Int)

    fun removeUserFromEvent(eventId: Int,kickedUserID: Int):Boolean
}
