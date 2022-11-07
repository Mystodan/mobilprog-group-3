package whenweekly.frontend.activities

import android.os.Bundle
import whenweekly.frontend.models.EventModel
import whenweekly.frontend.app.Globals
import whenweekly.frontend.databinding.ActivityEventBinding

class EventActivity : DrawerBaseActivity() {
    /**
     * Variable to be initiated later
     */
    private lateinit var binding: ActivityEventBinding      // Binding for the ActivityEvent


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * Sets the binding to the XML layout and sets it as the root
         */
        binding = ActivityEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /**
         * Intent used to receive data from a parcelable and set the content inside the layout XML to the data received
         */
        intent?.let {
            val model: EventModel = intent.getParcelableExtra(Globals.Constants.INFO)!!
            setActivityTitle(model.eventName)
            binding.eventTitle.text = model.eventName
            binding.eventStartDate.text = Globals.Utils.formatDate("yyyy.MM.dd", model.eventStart)
            binding.eventEndDate.text = Globals.Utils.formatDate("yyyy.MM.dd", model.eventEnd)
        }
    }
}