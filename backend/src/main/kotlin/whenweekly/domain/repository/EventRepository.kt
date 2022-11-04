package whenweekly.domain.repository

import whenweekly.domain.models.Event

interface EventRepository {
    fun addEvent(event: Event): Event
}
