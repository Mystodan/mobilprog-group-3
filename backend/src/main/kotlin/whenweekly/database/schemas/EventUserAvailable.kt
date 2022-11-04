package whenweekly.database.schemas

import org.ktorm.schema.Table
import org.ktorm.schema.*
import whenweekly.database.entities.EventUserAvailable

object EventUserAvailable: Table<EventUserAvailable>("event_user_available") {
    val event = int("event_id").primaryKey().references(Events) { it.event }
    val user = int("user_id").primaryKey().references(Users) { it.user }
    val time = datetime("time").bindTo{ it.time }
}
