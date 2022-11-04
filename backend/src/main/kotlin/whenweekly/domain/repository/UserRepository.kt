package whenweekly.domain.repository

import whenweekly.domain.models.User

interface UserRepository {
    fun addUser(user: User): User
    fun getAllUsers(): List<User>
}