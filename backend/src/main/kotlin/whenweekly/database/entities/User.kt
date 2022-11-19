package whenweekly.database.entities

import kotlinx.serialization.Serializable
import org.ktorm.entity.Entity
import java.util.*
import kotlin.reflect.KClass

interface User : Entity<User> {
    val id: Int?
    var uuid: ByteArray?
    val name: String
}