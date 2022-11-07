package whenweekly.frontend.models

import android.content.Context
import whenweekly.frontend.app.Globals
import java.util.*

class UserIDModel(private val context : Context) {
    private var key = context.resources.getString(Globals.Constants.USERID_KEY)
    var uuid: String? = null
    private var sharedPref = context.getSharedPreferences("WhenWeekly",Context.MODE_PRIVATE)

    init {
        uuid = sharedPref.getString(key,null)
        if (uuid == null) {
            uuid = genUUID()
            sharedPref.edit().putString(key, uuid).apply()
        }
    }

    private fun genUUID() = UUID.randomUUID().toString()
}
