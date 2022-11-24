package whenweekly.frontend.models

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.*
import androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.*
import whenweekly.frontend.app.Globals

// Tell it to clear the uuid (in case of database reset)
const val clearUUID: Boolean = false

class LocalUserModel(private val context : Context) {
    private var uuidKey = context.resources.getString(Globals.Constants.USERID_KEY)
    private var idKey = context.resources.getString(Globals.Constants.ID_KEY)
    private var uuid: String = String()
    private var id: Int = -1
    private val securePref = setSecurePref(Globals.Constants.SECURE_FILENAME,Globals.Constants.SECURE_MASTER_KEY_ALIAS)

    init {
        val storedId = securePref.getInt(idKey,-1)
        val storedUuid = securePref.getString(uuidKey,null)
        if (storedUuid != null && !clearUUID) {
            uuid = storedUuid
            Globals.Lib.LocalUUID = storedUuid
        }
        if (storedId != -1){
            id = storedId
            Globals.Lib.LocalID = storedId
        }
    }
    /**
     *  generates a random UUID as string
     */
    fun setUUID(uuid: String) {
        securePref.edit().putString(uuidKey, uuid).apply()
        Globals.Lib.LocalUUID = uuid
    }
    fun setID(id:Int) {
        securePref.edit().putInt(idKey, id).apply()
        Globals.Lib.LocalID = id
    }
    fun getID():Int = id
    fun getUUID():String = uuid
    /**
     *  sets encrypted shared preferences
     */
    private fun setSecurePref(fileName: String, alias: String) = EncryptedSharedPreferences.create(
        fileName, alias, context, AES256_SIV, AES256_GCM
    )
}
