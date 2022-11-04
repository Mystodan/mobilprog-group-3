package whenweekly.frontend

import android.app.Application


class ApplicationMain : Application(){
    override fun onCreate() {
        super.onCreate()
        val user = UserID(applicationContext);
        println(user.uuid)
    }

}