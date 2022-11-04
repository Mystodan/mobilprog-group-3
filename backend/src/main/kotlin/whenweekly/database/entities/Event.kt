package whenweekly.database.entities


import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import whenweekly.database.schemas.Events
import whenweekly.domain.models.Event

class EventDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EventDAO>(Events)

    var name by Events.name
    var description by Events.description
    var owner by UserDAO referencedOn Events.owner

    fun toModel() = Event(id.value, name, description, owner.toModel())
}