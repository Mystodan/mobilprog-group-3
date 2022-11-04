package whenweekly.database.schemas

import org.jetbrains.exposed.dao.id.IntIdTable

object Events: IntIdTable() {
    val name = varchar("name", 50)
    val description = varchar("description", 500)
    val owner = reference("owner", Users.id)
}