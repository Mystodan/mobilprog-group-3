package whenweekly.database

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import org.ktorm.dsl.*
import org.ktorm.entity.*
import whenweekly.database.entities.Event
import whenweekly.database.entities.User
import whenweekly.database.schemas.EventTable
import whenweekly.database.schemas.EventUserAvailableTable
import whenweekly.database.schemas.EventUserJoinedTable
import whenweekly.database.schemas.UserTable
import whenweekly.domain.manager.DatabaseManager
import whenweekly.misc.asBytes
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class DatabaseManagerImpl : DatabaseManager {
    private val database = DatabaseHelper.database()
    private val users get() = database.sequenceOf(UserTable)
    private val events get() = database.sequenceOf(EventTable)
    // Convert availableDates to json with jackson
    private val objectMapper = ObjectMapper()
    init {
        objectMapper.registerModule(JavaTimeModule().apply {
            addSerializer(
                LocalDateTime::class.java,
                LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
            )
        })
    }

    override fun addUser(user: User): User? {
        return try {
            users.add(user)
            user
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override fun getAllUsers(): List<User> {
        return try {
            users.toList()
        } catch (e: Exception) {
            println(e)
            emptyList()
        }
    }

    override fun getUserById(id: Int): User? {
        return try {
            users.first { it.id eq id }
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override fun updateUser(user: User): User? {
        return try {
            users.update(user)
            user
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override fun addEvent(event: Event): Event? {
        return try {
            events.add(event)
            event
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override fun getEventByInviteCode(inviteCode: String): Event? {
        return try {
            events.first { it.inviteCode eq inviteCode }
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override fun getEventById(id: Int): Event? {
        return try {
            events.first { it.id eq id }
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override fun getAllEvents(): List<Event> {
        return try {
            events.toList()
        } catch (e: Exception) {
            println(e)
            emptyList()
        }
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
        } catch (exception: Exception) {
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
    override fun removeUserFromEvent(eventId: Int, kickedUserID: Int): Boolean {
        return try {
            database.delete(EventUserJoinedTable) {
                (it.event eq eventId) and (it.user eq kickedUserID)
            } > 0
        } catch (exception: Exception) {
            println(exception)
            false
        }
    }

    override fun getEventsByUserId(userId: Int): List<Event> {
        return try {
            database.from(EventTable)
                .innerJoin(EventUserJoinedTable, on = EventTable.id eq EventUserJoinedTable.event).select()
                .where { EventUserJoinedTable.user eq userId }
                .map { EventTable.createEntity(it) }
        } catch (e: Exception) {
            println(e)
            emptyList()
        }
    }

    override fun getUsersByEventId(eventId: Int): List<User> {
        return try {
            database.from(UserTable)
                .innerJoin(EventUserJoinedTable, on = UserTable.id eq EventUserJoinedTable.user).select()
                .where { EventUserJoinedTable.event eq eventId }
                .map { UserTable.createEntity(it) }
        } catch (e: Exception) {
            println(e)
            emptyList()
        }
    }

    override fun getUserByUUID(uuid: String): User? {
        return try {
            val uuidBytes = UUID.fromString(uuid).asBytes()
            users.find { it.uuid eq uuidBytes }
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override fun resetDatabase() {
        try {
            database.deleteAll(EventUserJoinedTable)
            database.deleteAll(EventUserAvailableTable)
            database.deleteAll(EventTable)
            database.deleteAll(UserTable)
        } catch (e: Exception) {
            println(e)
        }
    }

    override fun deleteEventByID(eventId: Int): Boolean {
        return try {
            database.delete(EventTable) { it.id eq eventId }
            true
        } catch (e: Exception) {
            println(e)
            false
        }
    }

    override fun getAvailableDatesByEventId(eventId: Int): List<LocalDateTime> {
        return try {
            database.from(EventUserAvailableTable)
                .select()
                .where { EventUserAvailableTable.event eq eventId }
                .map { objectMapper.readValue(it[EventUserAvailableTable.available_dates], List::class.java) }
                .first()
                .map {
                    LocalDateTime.parse(it.toString())
                }
        } catch (e: Exception) {
            println(e)
            emptyList()
        }
    }

    override fun addAvailableDates(eventId: Int, userId: Int): Boolean {
        return try {
            database.insert(EventUserAvailableTable) {
                set(it.event, eventId)
                set(it.user, userId)
                set(it.available_dates, objectMapper.writeValueAsString(emptyList<LocalDateTime>()))
            } > 0
        } catch (e: Exception) {
            println(e)
            false
        }
    }
    override fun updateAvailableDates(eventId: Int, userId: Int, dates: List<LocalDateTime>): Boolean {
        return try {
            val jsonDates = objectMapper.writeValueAsString(dates)
            database.update(EventUserAvailableTable) {
                set(it.available_dates, jsonDates)
                where {
                    (it.event eq eventId) and (it.user eq userId)
                }
            } > 0
            true
        } catch (e: Exception) {
            println(e)
            false
        }
    }

    override fun removeAvailableDates(eventId: Int, userId: Int): Boolean {
        return try {
            database.delete(EventUserAvailableTable) {
                (it.event eq eventId) and (it.user eq userId)
            } > 0
        } catch (e: Exception) {
            println(e)
            false
        }
    }
}