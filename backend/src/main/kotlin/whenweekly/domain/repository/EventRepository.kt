package whenweekly.domain.repository

import whenweekly.database.entities.Event


interface EventRepository {
    fun addEvent(event: Event): Event
}
