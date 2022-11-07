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


    }
    object EventHolder{
        val Events = mutableListOf<EventModel>()
    }
    object Utils{
        fun formatDate(format:String, date:Long): String = SimpleDateFormat(format,Locale.ROOT).format(Date(date))
        fun getAllInvCodes():List<String> {
            var list:MutableList<String> = mutableListOf()
            EventHolder.Events.forEach{list.add(it.invCode)}
            return list
        }
    }


}