package whenweekly.frontend.models

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.*
import androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.*
import whenweekly.frontend.api.models.User
import whenweekly.frontend.app.Globals
import java.nio.ByteBuffer
import java.util.*

// Tell it to clear the uuid (in case of database reset)
const val clearUUID: Boolean = false

class LocalUserModel(private val context : Context) {
    private var uuidKey = context.resources.getString(Globals.Constants.USERID_KEY)
    private var idKey = context.resources.getString(Globals.Constants.ID_KEY)
    private var nameKey = context.resources.getString(Globals.Constants.USERNAME_KEY)
    private var user:User?=null
    private val securePref = setSecurePref(Globals.Constants.SECURE_FILENAME,Globals.Constants.SECURE_MASTER_KEY_ALIAS)

    init {
        val storedId = securePref.getInt(idKey,-1)
        val storedUuid = securePref.getString(uuidKey,null)
        val storedName = securePref.getString(nameKey, null)
        if (storedUuid != null && storedName != null && storedId != 0 && !clearUUID) {
            user = User(storedId, UUID.fromString(storedUuid).asBytes(), storedName)
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
        securePref.edit().putInt(idKey, user.id).apply()
        securePref.edit().putString(nameKey, user.name).apply()
        Globals.Lib.CurrentUser = user
    }
    private fun UUID.asBytes(): ByteArray {
        val b = ByteBuffer.wrap(ByteArray(16))
        b.putLong(this.mostSignificantBits)
        b.putLong(this.leastSignificantBits)
        return b.array()
    }
}
