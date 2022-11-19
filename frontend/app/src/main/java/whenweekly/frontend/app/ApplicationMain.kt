package whenweekly.frontend.app

import android.app.Application
import android.content.Intent
import whenweekly.frontend.activities.RegisterActivity
import whenweekly.frontend.models.LocalUserModel


class ApplicationMain : Application(){
    
    override fun onCreate() {
        super.onCreate()
        println(LocalUserModel(applicationContext).getUUID())
    }
}