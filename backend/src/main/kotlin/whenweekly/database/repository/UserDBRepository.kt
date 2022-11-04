package whenweekly.database.repository

import whenweekly.database.DatabaseManagerImpl
import whenweekly.database.entities.User
import whenweekly.domain.manager.DatabaseManager
import whenweekly.domain.repository.UserRepository

class UserDBRepository : UserRepository {
    private val database: DatabaseManager = DatabaseManagerImpl()

    override fun addUser(user: User): User {
        return database.addUser(user)
    }

    override fun getAllUsers(): List<User> {
        return database.getAllUsers()
    }

    override fun getUserById(id: Int): User? {
        return database.getUserById(id)
    }
}