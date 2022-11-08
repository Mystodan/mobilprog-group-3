package whenweekly.frontend.activities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import whenweekly.frontend.R
import whenweekly.frontend.models.EventModel
import whenweekly.frontend.app.Globals
import whenweekly.frontend.databinding.ActivityEventBinding

class EventActivity : DrawerBaseActivity() {
    /**
     * Variable to be initiated later
     */
    private lateinit var binding: ActivityEventBinding      // Binding for the ActivityEvent
    private lateinit var clipboard: ClipboardManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Sets the binding to the XML layout and sets it as the root
        binding = ActivityEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // gets the parcel from intent
        val eventInformation: EventModel = getParcelableFromIntent()?:return

        // sets the title of current activity
        setActivityTitle("Event: ${eventInformation.eventName}")

        // displays data from parcel to UI
        displayData(eventInformation)
        // reconfigures toolbar
        reconfigureToolbar();

        // sets up clipboard manager
        clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        binding.copyCode.setOnClickListener{clipboard.setPrimaryClip(
            ClipData.newPlainText(Globals.Constants.LABEL_CLIP_INV,eventInformation.invCode))
            Toast.makeText(this, "Copied: ${eventInformation.invCode}", Toast.LENGTH_SHORT).show()
        }




    }

    /**
     * Intent used to receive data from a parcelable and set the content inside the layout XML to the data received
     */
    private fun getParcelableFromIntent():EventModel?{
        return intent.getParcelableExtra(Globals.Constants.LABEL_PARCEL_INFO)
    }

    private fun displayData(model: EventModel){
        binding.eventTitle.text = model.eventName
        binding.eventStartDate.text = Globals.Utils.formatDate("yyyy.MM.dd", model.startDate)
        binding.eventEndDate.text = Globals.Utils.formatDate("yyyy.MM.dd", model.endDate)
        binding.code.text = model.invCode
    }
    private fun reconfigureToolbar(){
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_arrow)
        toolbar.setNavigationIconColor(resources.getColor(R.color.white))
        toolbar.setNavigationOnClickListener{ finish() }

    }

}