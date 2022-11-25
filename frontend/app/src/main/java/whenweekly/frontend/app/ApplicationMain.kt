package whenweekly.frontend.app

import android.app.Application
import whenweekly.frontend.models.LocalUserModel

class ApplicationMain : Application(){
    override fun onCreate() {
        println(LocalUserModel(applicationContext))
        super.onCreate()
    }
}