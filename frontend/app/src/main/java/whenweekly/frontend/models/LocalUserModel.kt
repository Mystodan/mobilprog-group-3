package whenweekly.frontend.models

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.*
import androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.*
import whenweekly.frontend.api.models.User
import whenweekly.frontend.app.Globals

// Tell it to clear the uuid (in case of database reset)
const val clearUUID: Boolean = false

class LocalUserModel(private val context : Context) {
    private var uuidKey = context.resources.getString(Globals.Constants.USERID_KEY)
    private val securePref = setSecurePref(Globals.Constants.SECURE_FILENAME,Globals.Constants.SECURE_MASTER_KEY_ALIAS)

    init {
        val storedUuid = securePref.getString(uuidKey,null)
        if (storedUuid != null && !clearUUID) {
            Globals.Lib.localUUID = storedUuid
        }
        else {
            println("No UUID stored")
        }

    }
    /**
     *  Sets encrypted shared preferences
     */
    private fun setSecurePref(fileName: String, alias: String) = EncryptedSharedPreferences.create(
        fileName, alias, context, AES256_SIV, AES256_GCM
    )

    /**
     * Sets user and UUID
     */
    fun setUser(user: User) {
        securePref.edit().putString(uuidKey, user.uuidToString()).apply()
        Globals.Lib.CurrentUser = user
        Globals.Lib.localUUID = user.uuidToString()
    }
}
