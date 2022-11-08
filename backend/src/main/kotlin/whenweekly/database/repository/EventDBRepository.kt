package whenweekly.database.repository

import whenweekly.database.DatabaseManagerImpl
import whenweekly.database.entities.Event
import whenweekly.domain.manager.DatabaseManager
import whenweekly.domain.repository.EventRepository
import java.util.*

class EventDBRepository : EventRepository {
    private val database: DatabaseManager = DatabaseManagerImpl()

    override fun addEvent(event: Event): Event {
        return database.addEvent(event)
    }

    override fun getEventById(id: Int): Event? {
        return database.getEventById(id)
    }

    override fun getAllEvents(): List<Event> {
        return database.getAllEvents()
    }

    override fun addUserToEvent(eventId: Int, userId: Int): Boolean {
        return database.addUserToEvent(eventId, userId)
    }

    override fun getEventsByUserId(userId: Int): List<Event> {
        return database.getEventsByUserId(userId)
    }
}
