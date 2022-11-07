package whenweekly.frontend.activities

import androidx.appcompat.R.anim.*
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import whenweekly.frontend.R
import whenweekly.frontend.app.Globals

open class DrawerBaseActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var toggle : ActionBarDrawerToggle
    private lateinit var toolbar: Toolbar

    private lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    lateinit var container : FrameLayout

    override fun setContentView(view: View?) {
        drawerLayout = layoutInflater.inflate(R.layout.activity_drawer_base,null) as DrawerLayout
        container  = drawerLayout.findViewById(R.id.activityContainer)
        container.addView(view)
        super.setContentView(drawerLayout)
        println(Globals.Constants.MOCKED_EXTERNAL_EVENTS.first().invCode)
        toolbar= drawerLayout.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toggle = ActionBarDrawerToggle(this,drawerLayout,toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        )
        drawerLayout.addDrawerListener(toggle)


        toolbar.setNavigationIconColor(resources.getColor(R.color.white))

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        navView = drawerLayout.findViewById(R.id.nav_view)
        navView.setNavigationItemSelectedListener(this)

    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        toggle.syncState()
    }
    override fun onNavigationItemSelected(@NonNull item: MenuItem): Boolean {
        val activityClass: Class<*>? = compareToCurrentActivity(when (item.itemId){
            R.id.nav_join -> EventJoinActivity::class.java
            R.id.nav_create -> CreatePlanActivity::class.java
            else -> EventListActivity::class.java
        })
        if (activityClass != null) startActivity(Intent(this,activityClass))
        overridePendingTransition(abc_fade_in, abc_fade_out)
        return false
    }





    private fun compareToCurrentActivity(activity:Class<*>):Class<*>?{
        if(javaClass == activity) return null
        return activity
    }


    private fun Toolbar.setNavigationIconColor(@ColorInt color: Int) = navigationIcon?.setTint(color)
    fun setActivityTitle(title: String){
        if (supportActionBar == null) return
        supportActionBar!!.title = title
    }
}