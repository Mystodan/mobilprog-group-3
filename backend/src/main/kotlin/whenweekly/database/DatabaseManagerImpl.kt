package whenweekly.database

import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList
import whenweekly.database.entities.Event
import whenweekly.database.entities.User
import whenweekly.database.schemas.Events
import whenweekly.database.schemas.Users
import whenweekly.domain.manager.DatabaseManager

class DatabaseManagerImpl : DatabaseManager {
    private val database = DatabaseHelper.database()

    private val users get() = database.sequenceOf(Users)
    private val events get() = database.sequenceOf(Events)
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
}