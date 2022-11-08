package whenweekly.database.schemas

import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.varchar
import whenweekly.database.entities.Event

object EventTable : Table<Event>("events") {
    val id = int("id").primaryKey().bindTo { it.id }
    val name = varchar("name").bindTo { it.name }
    val description = varchar("description").bindTo { it.description }
    val startDate = datetime("start_date").bindTo { it.start_date }
    val endDate = datetime("end_date").bindTo { it.end_date }
    val owner = int("owner").references(UserTable) { it.owner }
}
