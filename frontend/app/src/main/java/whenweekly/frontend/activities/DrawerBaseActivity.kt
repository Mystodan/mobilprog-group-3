package whenweekly.frontend.activities

import androidx.appcompat.R.anim.*
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.navigation.NavigationView
import whenweekly.frontend.fragments.EventListFragment
import whenweekly.frontend.R
import whenweekly.frontend.app.Globals
import whenweekly.frontend.fragments.EventCreateActivity
import whenweekly.frontend.fragments.EventJoinFragment


open class DrawerBaseActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var toggle : ActionBarDrawerToggle
    lateinit var toolbar: Toolbar

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var container : FrameLayout

    private var currFragment: Fragment? = Globals.Utils.startFragment
    private val fragmentManager: FragmentManager = supportFragmentManager


    override fun setContentView(view: View?) {
        drawerLayout = layoutInflater.inflate(R.layout.activity_drawer_base,null) as DrawerLayout
        container  = drawerLayout.findViewById(R.id.activityContainer)
        container.addView(view)
        super.setContentView(drawerLayout)
        toolbar= drawerLayout.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close)
        drawerLayout.addDrawerListener(toggle)


        toolbar.setNavigationIconColor(resources.getColor(R.color.white))

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        navView = drawerLayout.findViewById(R.id.nav_view)
        navView.setNavigationItemSelectedListener(this)

    }

    /**
     *
     */
    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        toggle.syncState()
    }

    /**
     *
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val componentClass: Class<*> = when (item.itemId) {
            R.id.nav_join -> EventJoinFragment::class.java
            R.id.nav_create -> EventCreateActivity::class.java
            else -> EventListFragment::class.java
        }

        loadFragment(componentClass)

        overridePendingTransition(abc_fade_in, abc_fade_out)

        return false
    }

    /**
     *
     */
    private fun loadFragment(fragmentClass:Class<*>?) {
        var fragment: Fragment? = null
        try {
            fragment = fragmentClass?.newInstance() as Fragment
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (fragment != null && currFragment != fragment) {
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit()
            currFragment = fragment
        }
    }

    /**
     *
     */
    fun Toolbar.setNavigationIconColor(@ColorInt color: Int) = navigationIcon?.setTint(color)

    /**
     *
     */
    fun setActivityTitle(title: String) {
        if (supportActionBar == null) return
        supportActionBar!!.title = title
    }
}