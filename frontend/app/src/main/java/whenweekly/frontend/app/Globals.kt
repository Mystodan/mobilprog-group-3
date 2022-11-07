package whenweekly.frontend.app

import whenweekly.frontend.R
import whenweekly.frontend.models.EventModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Constants for event title, event startDate, event endDate and information for parcelables
 */
class Globals {

    object Constants {
        const val USERID_KEY = (R.string.UIDKEY)
        const val LABEL_PARCEL_INFO = "EventActivityInformation"
        const val LABEL_CLIP_INV = "EventActivityInviteCode"
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
        val MOCKED_EXTERNAL_EVENTS = mutableListOf<EventModel>(
            EventModel("sucking", 1667433600000, 1668038400000),
        )

    }
    object Utils{
        fun formatDate(format:String, date:Long): String = SimpleDateFormat(format,Locale.ROOT).format(Date(date))
    }


}