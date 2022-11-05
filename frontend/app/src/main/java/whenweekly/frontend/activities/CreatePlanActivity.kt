package whenweekly.frontend.activities

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.util.Pair
import com.google.android.material.datepicker.MaterialDatePicker
import whenweekly.frontend.models.EventModel
import whenweekly.frontend.app.Globals
import whenweekly.frontend.R
import whenweekly.frontend.databinding.ActivityCreatePlanBinding



class CreatePlanActivity : DrawerBaseActivity() {

    private lateinit var binding: ActivityCreatePlanBinding
    private var startDate: Long = 0L
    private var endDate: Long = 0L
    private val invCodes = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCreatePlanBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setActivityTitle("Event Planner")
        // gets all inv codes
        Globals.Constants.MOCKED_EXTERNAL_EVENTS.forEach{
            invCodes.add(it.invCode)
        }
        // sets the functionality for picking dates
        binding.selectDates.setOnClickListener { showDataRangePicker() }
        // sets the functionality for creating an event
        binding.btnCreateEvent.setOnClickListener { createEventPlan() }
    }

    private fun showDataRangePicker() {

        var setupDatePicker = MaterialDatePicker
            .Builder.dateRangePicker()
            .setTitleText("Select Date")
        if(startDate != 0L || endDate != 0L) setupDatePicker = setupDatePicker.setSelection(Pair(startDate,endDate))
        val dateRangePicker = setupDatePicker.build()

        dateRangePicker.show(supportFragmentManager, "date_range_picker")
        dateRangePicker.addOnPositiveButtonClickListener { dateSelected ->
            startDate = dateSelected.first
            endDate = dateSelected.second
            setDateHolders(binding.startDateHolder, binding.endDateHolder)
        }
    }

    private fun createEventPlan(){
        val errString = mutableListOf<String>()
        if(binding.etEventName.text.isEmpty())
            errString.add("Please enter a name for the event and try again!")

        if(startDate == 0L || endDate == 0L)
            errString.add("Please select a date for the event and try again!")

        if (errString.isNotEmpty()){ errString.forEach{
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            Thread.sleep(1_00)
        }; return }
        Globals.Constants.EVENTS.add(createEvent(binding.etEventName.text.toString(), startDate, endDate))
        Toast.makeText(this, "Event added successfully!", Toast.LENGTH_SHORT).show()

        resetDateHolders(binding.startDateHolder, binding.endDateHolder,binding.etEventName)
    }

    private fun setDateHolders(startDateHolder: TextView?,endDateHolder: TextView?){
        fun formatDate(date:Long): String = Globals.Utils.formatDate("yy.MM.dd", date).replace(".","/")
        endDateHolder!!.text = formatDate(endDate)
        startDateHolder!!.text = formatDate(startDate)
    }
    private fun resetDateHolders(startDateHolder: TextView?,endDateHolder: TextView?, editTextHolder:EditText?){
       val def = getString(R.string.defaultDateHolder)
        editTextHolder!!.setText(def)
        endDateHolder!!.text = def
        startDateHolder!!.text = def
        startDate = 0L
        endDate = 0L
    }
    private fun createEvent(eventName:String, eventStart:Long, eventEnd:Long): EventModel {
        val ret = EventModel(eventName,eventStart,eventEnd)
        if(invCodes.contains(ret.invCode))createEvent(eventName, eventStart, eventEnd)
        return ret
    }


}