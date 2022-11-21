package whenweekly.domain.repository

import whenweekly.database.entities.User


interface UserRepository {
    fun addUser(user: User): User?
    fun getAllUsers(): List<User>
    fun getUserById(id: Int): User?

    fun getUserByUUID(uuid: String): User?

    fun updateUser(user: User): User?

    fun getUsersByEventId(eventId: Int): List<User>
}