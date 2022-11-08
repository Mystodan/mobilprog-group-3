package whenweekly.database.schemas

import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import whenweekly.database.entities.EventUserAvailable

object EventUserAvailableTable : Table<EventUserAvailable>("event_user_available") {
    val event = int("event_id").primaryKey().references(EventTable) { it.event }
    val user = int("user_id").primaryKey().references(UserTable) { it.user }
    val time = datetime("time").bindTo { it.time }
}
