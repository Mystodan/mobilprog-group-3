package whenweekly.frontend.app

import android.app.Activity
import android.app.Application
import whenweekly.frontend.models.UserIDModel


class ApplicationMain : Application(){
    override fun onCreate() {
        super.onCreate()
        val user = UserIDModel(applicationContext)
        println(user.uuid)
        Globals.Constants.MOCKED_EXTERNAL_EVENTS.addAll(Globals.Constants.EVENTS)
    }

    private var mCurrentActivity: Activity? = null
    fun getCurrentActivity(): Activity? {
        return mCurrentActivity
    }

    fun setCurrentActivity(mCurrentActivity: Activity?) {
        this.mCurrentActivity = mCurrentActivity
    }
}