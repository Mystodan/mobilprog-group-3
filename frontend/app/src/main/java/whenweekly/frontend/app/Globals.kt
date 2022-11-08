package whenweekly.frontend.app

import whenweekly.frontend.R
import whenweekly.frontend.models.EventModel
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Constants for event title, event startDate, event endDate and information for parcelables
 */
class Globals {

    object Constants {
        const val USERID_KEY = (R.string.UIDKEY)
        const val SECURE_FILENAME = "UserPrefs"
        const val SECURE_MASTERKEYALIAS = "WhenWeekly"
        const val LABEL_PARCEL_INFO = "EventActivityInformation"
        const val LABEL_CLIP_INV = "EventActivityInviteCode"
    }
    object Lib{
        val Events = mutableListOf<EventModel>()
        var userId :String? = null
    }
    object Utils{
        fun formatDate(format:String, date:Long): String = SimpleDateFormat(format,Locale.ROOT)
            .format(Date(date))

        fun getAllInvCodes():List<String> {
            var list:MutableList<String> = mutableListOf()
            Lib.Events.forEach{ it.invCode?.let { it1 -> list.add(it1) } }
            return list
        }
        fun createEvent(eventName:String, eventStart:Long, eventEnd:Long): EventModel? {
            if (Globals.Lib.userId == null) return null
            val ret = EventModel(eventName,eventStart,eventEnd)
            if(Globals.Utils.getAllInvCodes().contains(ret.invCode))createEvent(eventName, eventStart, eventEnd)
            return ret
        }
    }


}