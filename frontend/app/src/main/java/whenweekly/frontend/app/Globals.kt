package whenweekly.frontend.app

import whenweekly.frontend.models.EventModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Constants for event title, event startDate, event endDate and information for parcelables
 */
class Globals {

    object Constants {
        const val INFO = "info"
        val EVENTS = mutableListOf<EventModel>()
        val MOCKED_EXTERNAL_EVENTS = mutableListOf<EventModel>(
            EventModel("sucking", 1667433600000, 1668038400000),
        )

    }
    object Utils{
        fun formatDate(format:String, date:Long): String = SimpleDateFormat(format,Locale.ROOT).format(Date(date))
    }


}