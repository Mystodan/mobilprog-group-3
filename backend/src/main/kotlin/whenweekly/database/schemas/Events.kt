package whenweekly.database.schemas

import org.ktorm.schema.Table
import org.ktorm.schema.*
import whenweekly.database.entities.Event

object Events: Table<Event>("events") {
    val id = int("id").primaryKey().bindTo { it.id }
    val name = varchar("name").bindTo { it.name }
    val description = varchar("description").bindTo { it.description }
    val owner = int("owner").references(Users) { it.owner }
}
