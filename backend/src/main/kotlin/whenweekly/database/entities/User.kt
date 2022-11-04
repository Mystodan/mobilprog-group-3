package whenweekly.database.entities

import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import whenweekly.database.schemas.Users
import whenweekly.domain.models.User

class UserDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserDAO>(Users)

    var uuid by Users.uuid
    var name by Users.name

    fun toModel() = User(id.value, uuid, name)
}