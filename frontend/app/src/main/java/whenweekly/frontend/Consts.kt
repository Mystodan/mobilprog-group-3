package whenweekly.frontend

import java.text.SimpleDateFormat
import java.util.*

/**
 * Constants for event title, event startDate, event endDate and information for parcelables
 */
class Consts {
    object EventsConstants {
        const val information = "info"
        val EVENTS = mutableListOf<EventModel>(
            EventModel("Grocery shopping", 1667433600000, 1668038400000),
            EventModel("Watch movie", 1667433600000, 1668038400000),
            EventModel("Play games", 1667433600000, 1668038400000),
            EventModel("Clean the house", 1667433600000, 1668038400000),
            EventModel("Workout", 1667433600000, 1668038400000),
            EventModel("Project work", 1667433600000, 1668038400000),
            EventModel("Play golf", 1667433600000, 1668038400000),
            EventModel("Eat at a restaurant", 1667433600000, 1668038400000)
        )
    }

}