package whenweekly.database.repository

import whenweekly.database.DatabaseManagerImpl
import whenweekly.database.entities.User
import whenweekly.domain.manager.DatabaseManager
import whenweekly.domain.repository.UserRepository
import whenweekly.misc.asBytes
import java.nio.ByteBuffer
import java.util.*

class UserDBRepository : UserRepository {
    private val database: DatabaseManager = DatabaseManagerImpl()

    override fun addUser(user: User): User? {
        user.uuid = UUID.randomUUID().asBytes()
        return database.addUser(user)
    }

    override fun getAllUsers(): List<User> {
        return database.getAllUsers()
    }

    override fun getUserById(id: Int): User? {
        return database.getUserById(id)
    }

    override fun getUserByUUID(uuid: String): User? {
        return database.getUserByUUID(uuid)
    }

    override fun updateUser(user: User): User? { return database.updateUser(user) }

    override fun getUsersByEventId(eventId: Int): List<User> {
        return database.getUsersByEventId(eventId)
    }
}