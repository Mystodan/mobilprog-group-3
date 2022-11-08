package whenweekly.database

import org.ktorm.dsl.*
import org.ktorm.entity.add
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList
import whenweekly.database.entities.Event
import whenweekly.database.entities.User
import whenweekly.database.schemas.EventUserJoinedTable
import whenweekly.database.schemas.EventTable
import whenweekly.database.schemas.EventUserAvailableTable
import whenweekly.database.schemas.UserTable
import whenweekly.domain.manager.DatabaseManager
import java.time.LocalDateTime

class DatabaseManagerImpl : DatabaseManager {
    private val database = DatabaseHelper.database()

    private val users get() = database.sequenceOf(UserTable)
    private val events get() = database.sequenceOf(EventTable)

    private val eventUserJoined get() = database.sequenceOf(EventUserJoinedTable)
    override fun addUser(user: User): User {
        users.add(user)
        return user
    }

    override fun getAllUsers(): List<User> {
        return users.toList()
    }

    override fun getUserById(id: Int): User? {
        return users.find { it.id eq id }
    }

    override fun addEvent(event: Event): Event {
        events.add(event)
        return event
    }

    override fun getEventById(id: Int): Event? {
        return events.find { it.id eq id }
    }

    override fun getAllEvents(): List<Event> {
        return events.toList()
    }

    override fun addUserToEvent(eventId: Int, userId: Int): Boolean {
        return database.insert(EventUserJoinedTable) {
            set(it.event, eventId)
            set(it.user, userId)
            set(it.join_time, LocalDateTime.now())
        } > 0
    }

    override fun getEventsByUserId(userId: Int): List<Event> {
        return database.from(EventTable)
                .innerJoin(EventUserJoinedTable, on = EventTable.id eq EventUserJoinedTable.event).select()
                .where { EventUserJoinedTable.user eq userId }
                .map { EventTable.createEntity(it) }
    }

    override fun resetDatabase() {
        database.deleteAll(EventUserJoinedTable)
        database.deleteAll(EventUserAvailableTable)
        database.deleteAll(EventTable)
        database.deleteAll(UserTable)
    }
}