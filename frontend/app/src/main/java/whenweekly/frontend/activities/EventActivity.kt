package whenweekly.frontend.activities

import android.content.*
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import whenweekly.frontend.R
import whenweekly.frontend.app.Globals
import whenweekly.frontend.databinding.ActivityEventBinding
import whenweekly.frontend.fragments.*
import whenweekly.frontend.models.EventModel

class EventActivity : DrawerBaseActivity() {
    enum class ButtonPanel{
        Admin, DatesSelect, DatesAll, Leave
    }

    /**
     * Variable to be initiated later
     */
    private lateinit var binding: ActivityEventBinding      // Binding for the ActivityEvent
    private lateinit var clipboard: ClipboardManager
    private var isOwner = false
    private var buttonPanel: ButtonPanel = ButtonPanel.DatesAll
    private var currFragment: Fragment = DateViewAllFragment()
    private val fragmentManager: FragmentManager = supportFragmentManager
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        isOwner = false
        super.onCreate(savedInstanceState)
        //Sets the binding to the XML layout and sets it as the root
        binding = ActivityEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // gets the parcel from intent
        val eventInformation: EventModel = getParcelableFromIntent()?:return
        // gets the permissions
        manageOwnerState(eventInformation)
        // sets the title of current activity
        setActivityTitle("Event: ${eventInformation.eventName}")
        // displays data from parcel to UI
        displayData(eventInformation)
        // reconfigures toolbar
        reconfigureToolbar()
        // set default fragment
        loadFragment(currFragment::class.java, eventInformation)

        // sets up clipboard manager
        clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        binding.copyCode.setOnClickListener { clipboard.setPrimaryClip(
            ClipData.newPlainText(Globals.Constants.LABEL_CLIP_INV,eventInformation.invCode))
            Toast.makeText(this, "Copied: ${eventInformation.invCode}", Toast.LENGTH_SHORT).show()
        }


    }
    /**
     * Intent used to receive data from a parcelable and set the content inside the layout XML to the data received
     */
    private fun getParcelableFromIntent():EventModel? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(Globals.Constants.LABEL_PARCEL_INFO, EventModel::class.java)
        } else {
            intent.getParcelableExtra(Globals.Constants.LABEL_PARCEL_INFO)
        }


    private fun manageOwnerState(model:EventModel){
        applyUserAccess(model)
        if(model.ownerId != Globals.Lib.CurrentUser?.id){ hideAdmin(model);return}
        applyOwnerAccess(model)
    }


    private fun applyUserAccess(model: EventModel){
        binding.dateManage.setOnClickListener {
            buttonPanel = ButtonPanel.DatesSelect
            changePanelView(buttonPanel, model)
        }
        binding.dateView.setOnClickListener {
            buttonPanel = ButtonPanel.DatesAll
            changePanelView(buttonPanel, model)
        }
    }
    private fun applyOwnerAccess(model:EventModel){
        isOwner = true
        binding.leave.visibility = android.view.View.GONE
        binding.Admin.setOnClickListener {
            buttonPanel = ButtonPanel.Admin
            changePanelView(buttonPanel, model)

        }
    }
    private fun hideAdmin(model:EventModel){
        if(!isOwner){
            binding.Admin.visibility = android.view.View.GONE
            binding.leave.setOnClickListener {
                buttonPanel = ButtonPanel.Leave
                changePanelView(buttonPanel,model)
            }
        }
    }

    /**
     *
     */
    private fun displayData(model: EventModel) {
        binding.eventTitle.text = model.eventName
        binding.eventStartDate.text = Globals.Utils.formatDate("yyyy.MM.dd", model.startDate)
        binding.eventEndDate.text = Globals.Utils.formatDate("yyyy.MM.dd", model.endDate)
        binding.code.text = model.invCode
    }
    /**
     *
     */
    private fun reconfigureToolbar() {
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_arrow)
        toolbar.setNavigationIconColor(resources.getColor(R.color.white, theme))
        toolbar.setNavigationOnClickListener{ finish()}
    }

    private fun changePanelView(menuItem: ButtonPanel, parcel: EventModel){
        val componentClass: Class<*> = when(menuItem){
            ButtonPanel.Admin -> EventAdminFragment::class.java
            ButtonPanel.DatesAll -> DateViewAllFragment::class.java
            ButtonPanel.Leave-> EventLeaveFragment::class.java
            else -> DateReportFragment::class.java
        }

        loadFragment(componentClass, parcel)
    }
    private fun loadFragment(fragmentClass:Class<*>?, parcel: EventModel) {
        var fragment: Fragment? = null
        try {
            fragment = fragmentClass?.newInstance() as Fragment
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (fragment != null && currFragment != fragment) {
            val eventBundle = Bundle()
            eventBundle.putParcelable(Globals.Constants.LABEL_PARCEL_INFO,parcel)
            fragment.arguments = eventBundle
            fragmentManager.beginTransaction().replace(R.id.fragmentHolder, fragment).commit()
            currFragment = fragment
        }
    }
}