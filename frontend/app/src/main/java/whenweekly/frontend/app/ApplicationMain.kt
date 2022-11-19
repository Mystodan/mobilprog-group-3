package whenweekly.frontend.app

import android.app.Application
import whenweekly.frontend.models.LocalUserModel


class ApplicationMain : Application(){
    
    override fun onCreate() {
        super.onCreate()
        Globals.Lib.userId = LocalUserModel(applicationContext).uuid
        println(Globals.Lib.userId)
    }
}