package whenweekly.database.repository

import whenweekly.database.DatabaseManagerImpl
import whenweekly.database.entities.Event
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

    override fun addEvent(event: Event): Event {
        event.inviteCode = genInvCode()
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
