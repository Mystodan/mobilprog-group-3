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
        const val ID_KEY = (R.string.IDKEY)
        const val SECURE_FILENAME = "UserPrefs"
        const val SECURE_MASTER_KEY_ALIAS = "WhenWeekly"
        const val LABEL_PARCEL_INFO = "EventActivityInformation"
        const val LABEL_CLIP_INV = "EventActivityInviteCode"

    }
    object Lib{
        var Events = mutableListOf<EventModel>()
        var LocalUUID: String = ""
        var LocalID: Int?=null

    }
    object Utils{
        fun formatDate(format:String, date:Long): String = SimpleDateFormat(format,Locale.ROOT)
            .format(Date(date))
        val startFragment = EventListFragment()

    }


}