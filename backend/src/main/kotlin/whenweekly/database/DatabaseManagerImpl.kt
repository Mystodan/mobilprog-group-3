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

/**
 * Database manager impl
 *
 * @constructor Create empty Database manager impl
 */
class DatabaseManagerImpl : DatabaseManager {
    private val database = DatabaseHelper.database()
    private val users get() = database.sequenceOf(UserTable)
    private val events get() = database.sequenceOf(EventTable)

    // Convert availableDates to json with jackson
    private val objectMapper = ObjectMapper()

    init {
        // Initialize jackson with java LocalDateTime serialization support
        objectMapper.registerModule(JavaTimeModule().apply {
            addSerializer(
                LocalDateTime::class.java,
                LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
            )
        })
    }

    /**
     * Add user
     *
     * @param user The user to add
     * @return The added user
     */
    override fun addUser(user: User): User? {
        return try {
            users.add(user)
            user
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    /**
     * Get all users
     *
     * @return List of users
     */
    override fun getAllUsers(): List<User> {
        return try {
            users.toList()
        } catch (e: Exception) {
            println(e)
            emptyList()
        }
    }

    /**
     * Get user by id
     *
     * @param id The id of the user
     * @return The user with the given id
     */
    override fun getUserById(id: Int): User? {
        return try {
            users.first { it.id eq id }
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    /**
     * Update user
     *
     * @param user The user to update
     * @return The updated user
     */
    override fun updateUser(user: User): User? {
        return try {
            users.update(user)
            user
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    /**
     * Add event
     *
     * @param event The event to add
     * @return The added event
     */
    override fun addEvent(event: Event): Event? {
        return try {
            events.add(event)
            event
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    /**
     * Get event by invite code
     *
     * @param inviteCode The invite codee of the event
     * @return The event with the invite code
     */
    override fun getEventByInviteCode(inviteCode: String): Event? {
        return try {
            events.first { it.inviteCode eq inviteCode }
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    /**
     * Get event by id
     *
     * @param id The id of the event to get
     * @return The event with the given id
     */
    override fun getEventById(id: Int): Event? {
        return try {
            events.first { it.id eq id }
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    /**
     * Get all events
     *
     * @return List of all events
     */
    override fun getAllEvents(): List<Event> {
        return try {
            events.toList()
        } catch (e: Exception) {
            println(e)
            emptyList()
        }
    }

    /**
     * Add user to event
     *
     * @param eventId The event id to add user to
     * @param userId The user id to add to event
     * @return The event user joined
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
     * Remove user from event
     *
     * @param eventId The event id
     * @param kickedUserID The user id of the user to be removed
     * @return True if the user was removed successfully
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

    /**
     * Get events by user id
     *
     * @param userId The user id to get events for
     * @return List of events
     */
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

    /**
     * Get users by event id
     *
     * @param eventId The event id to get users from
     * @return List of users
     */
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

    /**
     * Get user by UUID
     *
     * @param uuid The uuid of the user
     * @return The user
     */
    override fun getUserByUUID(uuid: String): User? {
        return try {
            val uuidBytes = UUID.fromString(uuid).asBytes()
            users.find { it.uuid eq uuidBytes }
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    /**
     * Reset the database
     *
     */
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

    /**
     * Delete event by i d
     *
     * @param eventId The event id to delete
     * @return True if successful, false otherwise
     */
    override fun deleteEventByID(eventId: Int): Boolean {
        return try {
            database.delete(EventTable) { it.id eq eventId }
            true
        } catch (e: Exception) {
            println(e)
            false
        }
    }

    /**
     * Get available dates by event id
     *
     * @param eventId The event id to get available dates for
     * @return The list of available dates
     */
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

    /**
     * Add available dates
     *
     * @param eventId The event id to add available dates for
     * @param userId The user id to add available dates for
     * @return True if success, false otherwise
     */
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

    /**
     * Update available dates
     *
     * @param eventId The event id to update available dates for
     * @param userId The user id to update available dates for
     * @param dates The dates to add to the available dates for the user
     * @return True if successful, false otherwise
     */
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

    /**
     * Remove available dates
     *
     * @param eventId The event id to remove available dates for
     * @param userId The user id to remove available dates for
     * @return True if successful, false otherwise
     */
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