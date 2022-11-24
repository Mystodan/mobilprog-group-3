package whenweekly.frontend.models

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.*
import androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.*
import whenweekly.frontend.api.Api
import whenweekly.frontend.api.models.User
import whenweekly.frontend.app.Globals
import java.nio.ByteBuffer
import java.util.*

// Tell it to clear the uuid (in case of database reset)
const val clearUUID: Boolean = false

class LocalUserModel(private val context : Context) {
    private var uuidKey = context.resources.getString(Globals.Constants.USERID_KEY)
    private var user:User?=null
    private val securePref = setSecurePref(Globals.Constants.SECURE_FILENAME,Globals.Constants.SECURE_MASTER_KEY_ALIAS)

    init {
        val storedUuid = securePref.getString(uuidKey,null)
        if (storedUuid != null && !clearUUID) {
            Globals.Lib.localUUID = storedUuid
            //user = Api.getUser(storedUuid)
            Globals.Lib.CurrentUser = user
        }

    }
    /**
     *  sets encrypted shared preferences
     */
    private fun setSecurePref(fileName: String, alias: String) = EncryptedSharedPreferences.create(
        fileName, alias, context, AES256_SIV, AES256_GCM
    )

    fun setUser(user: User) {
        securePref.edit().putString(uuidKey, user.uuidToString()).apply()
        Globals.Lib.CurrentUser = user
    }
}
