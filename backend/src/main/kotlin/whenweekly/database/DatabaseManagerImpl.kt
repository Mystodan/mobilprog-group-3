package whenweekly.database

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import whenweekly.domain.manager.DatabaseManager
import whenweekly.database.entities.*
import whenweekly.database.schemas.*
import whenweekly.domain.models.Event
import whenweekly.domain.models.User

class DatabaseManagerImpl : DatabaseManager {
    init {
        DatabaseHelper.connect()
        transaction{
            SchemaUtils.create(Users, Events)
        }
    }

    override fun addUser(user: User): User {
        return transaction {
            UserDAO.new {
                uuid = user.uuid
                name = user.name
            }.toModel()
        }
    }

    override fun getAllUsers(): List<User> {
        return transaction {
            UserDAO.all().map{it.toModel()}
        }
    }

    override fun addEvent(event: Event): Event {
        return transaction {
            EventDAO.new {
                name = event.name
                description = event.description
                owner = UserDAO[event.owner.id]
            }.toModel()
        }
    }
}