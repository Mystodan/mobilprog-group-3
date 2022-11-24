package whenweekly.database.schemas

import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.text
import whenweekly.database.entities.EventUserAvailable

object EventUserAvailableTable : Table<EventUserAvailable>("event_user_available") {
    val event = int("event_id").primaryKey().references(EventTable) { it.event }
    val user = int("user_id").primaryKey().references(UserTable) { it.user }
    val available_dates = text("available_dates").bindTo { it.available_dates }
}
