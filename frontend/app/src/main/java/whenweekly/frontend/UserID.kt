package whenweekly.frontend

import android.content.Context
import java.util.*

class UserID(private val context : Context) {
    private var key = "177013"
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
