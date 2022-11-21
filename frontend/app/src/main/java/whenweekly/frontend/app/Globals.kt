package whenweekly.frontend.app

import whenweekly.frontend.fragments.EventListFragment
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
        const val SECURE_FILENAME = "UserPrefs"
        const val SECURE_MASTER_KEY_ALIAS = "WhenWeekly"
        const val LABEL_PARCEL_INFO = "EventActivityInformation"
        const val LABEL_CLIP_INV = "EventActivityInviteCode"

    }
    object Lib{
        val Events = mutableListOf<EventModel>()
        var userId: String = ""
    }
    object Utils{
        fun formatDate(format:String, date:Long): String = SimpleDateFormat(format,Locale.ROOT)
            .format(Date(date))
        val startFragment = EventListFragment()
        private fun getAllInvCodes():List<String> {
            val list: MutableList<String> = mutableListOf()
            Lib.Events.forEach{ it.invCode.let { it1 -> list.add(it1) } }
            return list
        }
        fun createEvent(eventName:String, eventStart:Long, eventEnd:Long, inviteCode: String): EventModel {
            val ret = EventModel(eventName,eventStart,eventEnd, inviteCode)
            if(getAllInvCodes().contains(ret.invCode)) createEvent(eventName, eventStart, eventEnd, inviteCode)
            return ret
        }
    }


}