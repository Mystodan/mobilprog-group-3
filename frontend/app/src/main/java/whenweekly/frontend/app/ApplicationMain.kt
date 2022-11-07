package whenweekly.frontend.app

import android.app.Activity
import android.app.Application
import whenweekly.frontend.models.UserIDModel


class ApplicationMain : Application(){
    override fun onCreate() {
        super.onCreate()
        val user = UserIDModel(applicationContext)
        println(user.uuid)
    }

}