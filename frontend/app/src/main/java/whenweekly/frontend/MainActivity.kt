package whenweekly.frontend

import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem

import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.navigation.NavigationView
import whenweekly.frontend.databinding.ActivityMainBinding
import java.lang.Exception


class MainActivity : AppCompatActivity() {
    private lateinit var mDrawer:DrawerLayout
    private lateinit var nvDrawer:NavigationView
    private lateinit var binding : ActivityMainBinding
    private var currFragment: Fragment? = null
    private val fragmentManager: FragmentManager = supportFragmentManager
    override fun onCreate(savedInstanceState: Bundle?) {
        title = "Lobbies"
        super.onCreate(savedInstanceState)
        // setup binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // replaces previous actionbar with current Toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val hMenu: Drawable = resources.getDrawable(R.drawable.hmenu)
        hMenu.setTint(Color.WHITE)
        supportActionBar?.setHomeAsUpIndicator(hMenu)

        // find our drawer view
        mDrawer = findViewById(R.id.drawer_layout)
        // find our navigation drawer view
        nvDrawer = findViewById(R.id.nvView)
        // Setup drawer view
        setupDrawerContent(nvDrawer)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                mDrawer.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            selectDrawerItem(menuItem)
            true
        }
    }

    private fun selectDrawerItem(menuItem: MenuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        var fragment: Fragment? = null
        val fragmentClass: Class<*>
        if (menuItem.itemId == R.id.nav_first_fragment && currFragment != null) {
            fragmentManager.beginTransaction().remove(currFragment!!).commit()
            title = menuItem.title
            mDrawer.closeDrawers()
            return
        } else
            fragmentClass = when (menuItem.itemId) {
            R.id.nav_first_fragment -> MainActivity::class.java
            R.id.nav_second_fragment -> JoinEventPlanFragment::class.java
            R.id.nav_third_fragment -> CreateEventPlanFragment::class.java
            else -> MainActivity::class.java
        }
        try {
            fragment = fragmentClass.newInstance() as Fragment
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Insert the fragment by replacing any existing fragment

        if (fragment != null) {
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit()
            currFragment = fragment
        }

        // Highlight the selected item has been done by NavigationView
        menuItem.isChecked = true
        // Set action bar title
        title = menuItem.title
        // Close the navigation drawer
        mDrawer.closeDrawers()
    }


}