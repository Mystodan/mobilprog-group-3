package whenweekly.frontend

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import whenweekly.frontend.databinding.ActivityEventBinding
import java.text.SimpleDateFormat
import java.util.*

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
            val model:EventModel = intent.getParcelableExtra(Consts.EventsConstants.information)!!

            setActivityTitle(model.eventName)
            binding.eventTitle.text = model.eventName
            binding.eventStartDate.text = SimpleDateFormat("yyyy.MM.dd HH:mm").format(Date(model.eventStart))
            binding.eventEndDate.text = SimpleDateFormat("yyyy.MM.dd HH:mm").format(Date(model.eventEnd))
        }
    }
}