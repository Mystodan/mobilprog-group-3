package whenweekly.database.repository

import whenweekly.database.DatabaseManagerImpl
import whenweekly.database.entities.Event
import whenweekly.domain.manager.DatabaseManager
import whenweekly.domain.repository.EventRepository

class EventDbRepository : EventRepository {
    private val database: DatabaseManager = DatabaseManagerImpl()

    override fun addEvent(event: Event): Event {
        return database.addEvent(event)
    }
}
