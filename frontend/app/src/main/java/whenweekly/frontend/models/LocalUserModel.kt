package whenweekly.frontend.models

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.*
import androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.*
import whenweekly.frontend.app.Globals
import java.util.*

class LocalUserModel(private val context : Context) {
    private var key = context.resources.getString(Globals.Constants.USERID_KEY)
    var uuid: String? = null
    private val securePref = setSecurePref(Globals.Constants.SECURE_FILENAME,Globals.Constants.SECURE_MASTER_KEY_ALIAS)

    init{
        uuid = securePref.getString(key,null)
    }
    /**
     *  generates a random UUID as string
     */
    fun genUUID() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString()
            securePref.edit().putString(key, uuid).apply()
        }
    }
    fun setGlobalUserID (){
        if (uuid != null) Globals.Lib.userId= uuid
    }

    /**
     *  sets encrypted shared preferences
     */
    private fun setSecurePref(fileName: String, alias: String) = EncryptedSharedPreferences.create(
        fileName, alias, context, AES256_SIV, AES256_GCM
    )
}
