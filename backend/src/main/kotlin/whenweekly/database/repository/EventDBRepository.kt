package whenweekly.database.repository

import whenweekly.database.DatabaseManagerImpl
import whenweekly.database.entities.Event
import whenweekly.database.entities.User
import whenweekly.domain.manager.DatabaseManager
import whenweekly.domain.repository.EventRepository
import java.util.*

// TODO: move somewhere else
private fun genInvCode(): String {
    fun getRandNum(min:Int, max:Int):Int = Random().nextInt(max+1)+min
    var invCode = ""
    val symbols = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
    for (i in 0..19){
        invCode += symbols[getRandNum(0,symbols.length-1)]
    }
    return invCode
}
class EventDBRepository : EventRepository {
    private val database: DatabaseManager = DatabaseManagerImpl()

    override fun addEvent(event: Event, owner: User): Event? {
        event.inviteCode = genInvCode()
        event.owner = owner
        val newEvent = database.addEvent(event);
        if (newEvent != null) {
            // Add owner to event
            database.addUserToEvent(newEvent.id, owner.id)

            // Get full owner info
            database.getUserById(owner.id)?.let {
                newEvent.owner = it
            }
        }
        return newEvent
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

    override fun deleteEventByID(eventId: Int): Boolean {
        return database.deleteEventByID(eventId)
    }

    override fun removeUserFromEvent(eventId: Int, kickedUserID: Int): Boolean {
        return database.removeUserFromEvent(eventId, kickedUserID)
    }
}
