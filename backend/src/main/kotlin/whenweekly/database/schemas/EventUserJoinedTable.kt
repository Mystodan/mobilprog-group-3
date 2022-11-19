package whenweekly.database.schemas

import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import whenweekly.database.entities.EventUserJoined

object EventUserJoinedTable : Table<EventUserJoined>("event_user_joined") {
    val event = int("event_id").primaryKey().references(EventTable) { it.event }
    val user = int("user_id").primaryKey().references(UserTable) { it.user }
    val join_time = datetime("join_time").bindTo { it.join_time }
}
