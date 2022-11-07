package whenweekly.domain.repository

import whenweekly.database.entities.Event


interface EventRepository {
    fun addEvent(event: Event): Event

    fun getEventById(id: Int): Event?

    fun getAllEvents(): List<Event>
}
