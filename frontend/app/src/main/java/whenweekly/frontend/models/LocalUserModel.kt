package whenweekly.frontend.models

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.*
import androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.*
import whenweekly.frontend.app.Globals
import java.util.*

class LocalUserModel(private val context : Context) {
    private var key = context.resources.getString(Globals.Constants.USERID_KEY)
    private var uuid: String = String()
    private val securePref = setSecurePref(Globals.Constants.SECURE_FILENAME,Globals.Constants.SECURE_MASTER_KEY_ALIAS)

    init {
        val storedUuid = securePref.getString(key,null)
        if (storedUuid != null) {
            uuid = storedUuid;
            println("UUID: " + uuid.toString())
        }
    }
    /**
     *  generates a random UUID as string
     */
    fun genUUID() {
        uuid = UUID.randomUUID().toString()
        securePref.edit().putString(key, uuid).apply()
    }
    fun setGlobalUserID (){
        Globals.Lib.userId= uuid
    }

    /**
     *  sets encrypted shared preferences
     */
    private fun setSecurePref(fileName: String, alias: String) = EncryptedSharedPreferences.create(
        fileName, alias, context, AES256_SIV, AES256_GCM
    )
}
