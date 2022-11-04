package whenweekly.database.repository

import whenweekly.database.DatabaseManagerImpl
import whenweekly.domain.manager.DatabaseManager
import whenweekly.domain.models.User
import whenweekly.domain.repository.UserRepository

class UserDBRepository : UserRepository {
    private val database: DatabaseManager = DatabaseManagerImpl()

    override fun addUser(user: User): User {
        return database.addUser(user)
    }

    override fun getAllUsers(): List<User> {
        return database.getAllUsers()
    }
}