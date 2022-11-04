package whenweekly.database.schemas

import org.jetbrains.exposed.dao.id.IntIdTable

object Users: IntIdTable() {
    val uuid = binary("uuid", 16)
    val name = varchar("name", 50)
}
