package whenweekly.database

import org.ktorm.dsl.*
import org.ktorm.entity.*
import whenweekly.database.entities.Event
import whenweekly.database.entities.User
import whenweekly.database.schemas.EventUserJoinedTable
import whenweekly.database.schemas.EventTable
import whenweekly.database.schemas.EventUserAvailableTable
import whenweekly.database.schemas.UserTable
import whenweekly.domain.manager.DatabaseManager
import java.nio.ByteBuffer
import java.time.LocalDateTime
import java.util.*

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

    /**
     * add a selected user from the selected event
     *
     * @param eventId       - ID of the event in EventUserJoinedTable
     * @param userId        - ID of the user in EventUserJoinedTable to add
     */
    override fun addUserToEvent(eventId: Int, userId: Int): Boolean {
        return try {
            database.insert(EventUserJoinedTable) {
                set(it.event, eventId)
                set(it.user, userId)
                set(it.join_time, LocalDateTime.now())
            } > 0
        } catch ( exception: Exception ) {
            println(exception)
            false
        }
    }

    /**
     * Remove a selected user from the selected event
     *
     * @param eventId       - ID of the event in EventUserJoinedTable
     * @param kickedUserID  - ID of the user in EventUserJoinedTable to remove
     */
    override fun removeUserFromEvent(eventId: Int,  kickedUserID: Int): Boolean {
         return database.delete(EventUserJoinedTable){(it.event eq eventId) and (it.user eq kickedUserID)} > 0
    }

    override fun getEventsByUserId(userId: Int): List<Event> {
        return database.from(EventTable)
                .innerJoin(EventUserJoinedTable, on = EventTable.id eq EventUserJoinedTable.event).select()
                .where { EventUserJoinedTable.user eq userId }
                .map { EventTable.createEntity(it) }
    }

    private fun UUID.asBytes(): ByteArray{
        val b = ByteBuffer.wrap(ByteArray(16))
        b.putLong(this.mostSignificantBits)
        b.putLong(this.leastSignificantBits)
        return b.array()
    }
    override fun getUserByUUID(uuid: String): User? {
        val uuidBytes = UUID.fromString(uuid).asBytes()
        return users.find { it.uuid eq uuidBytes }
    }

    override fun resetDatabase() {
        database.deleteAll(EventUserJoinedTable)
        database.deleteAll(EventUserAvailableTable)
        database.deleteAll(EventTable)
        database.deleteAll(UserTable)
    }

    override fun deleteEventByID(eventId: Int) {
        //EventTable has a Primary key with ON DELETE CASCADE, should delete the child tables as well
        database.delete(EventTable) { it.id eq eventId }
        //database.delete(EventUserJoinedTable) { it.event eq eventId }
        //database.delete(EventUserAvailableTable) { it.event eq eventId }
    }
}