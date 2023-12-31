package whenweekly.frontend.app

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import whenweekly.frontend.fragments.EventListFragment
import whenweekly.frontend.R
import whenweekly.frontend.activities.EventActivity
import whenweekly.frontend.api.models.User
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
    object Lib {
        var Events = mutableListOf<EventModel>()
        var localUUID: String = ""
        var CurrentUser : User?=null
        var isDarkMode = false
    }

    object Utils {
        val startFragment = EventListFragment()
        fun formatDate(format:String, date:Long): String = SimpleDateFormat(format,Locale.ROOT).format(Date(date))

        /**
         * Starts the EventActivty of the event that has been clicked
         */
        fun startEventActivityOfEvent(input: EventModel, activity: FragmentActivity, launcher: ActivityResultLauncher<Intent>) {
            val intent = Intent(activity, EventActivity::class.java)
            intent.putExtra(Constants.LABEL_PARCEL_INFO, input)
            launcher.launch(intent)
        }
    }
}