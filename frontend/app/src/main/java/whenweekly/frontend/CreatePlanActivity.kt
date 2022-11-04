package whenweekly.frontend

import android.os.Bundle
import android.widget.Toast
import com.google.android.material.datepicker.MaterialDatePicker
import whenweekly.frontend.databinding.ActivityCreatePlanBinding


class CreatePlanActivity : DrawerBaseActivity() {

    private lateinit var binding: ActivityCreatePlanBinding
    private var startDate: Long = 0L
    private var endDate: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCreatePlanBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        
        setActivityTitle("Event Planner")

        binding.selectDates.setOnClickListener {
            showDataRangePicker()
        }

        binding.btnCreateEvent.setOnClickListener {
            if(binding.etEventName.text.isEmpty()) {
                Toast.makeText(this, "Please enter a name for the event and try again!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(startDate == 0L || endDate == 0L) {
                Toast.makeText(this, "Please select dates and try again!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val eventModel = EventModel(binding.etEventName.text.toString(), startDate, endDate)
            Consts.EventsConstants.EVENTS.add(eventModel)
            binding.etEventName.setText("")
            Toast.makeText(this, "Event added successfully!", Toast.LENGTH_SHORT).show()
            startDate = 0L
            endDate = 0L
        }
    }

    private fun showDataRangePicker() {
        val dateRangePicker = MaterialDatePicker
            .Builder.dateRangePicker()
            .setTitleText("Select Date")
            .setTheme(R.style.MaterialDatePickerTheme)
            .build()

        dateRangePicker.show(supportFragmentManager, "date_range_picker")

        dateRangePicker.addOnPositiveButtonClickListener { dateSelected ->

            startDate = dateSelected.first
            endDate = dateSelected.second
        }
    }
}