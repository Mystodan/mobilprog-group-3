package whenweekly.database.entities

import org.ktorm.entity.Entity

interface User : Entity<User> {
    val id: Int
    val uuid: ByteArray
    val name: String
}
