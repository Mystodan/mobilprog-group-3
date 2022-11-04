package whenweekly.database.schemas

import org.ktorm.schema.Table
import org.ktorm.schema.*
import whenweekly.database.entities.User

object Users: Table<User>("users") {
    val id = int("id").primaryKey().bindTo{ it.id }
    val uuid = bytes("uuid").bindTo{ it.uuid }
    val name = varchar("name").bindTo{ it.name }
}
