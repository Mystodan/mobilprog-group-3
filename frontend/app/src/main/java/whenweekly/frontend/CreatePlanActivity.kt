package whenweekly.frontend

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.util.Pair
import com.google.android.material.datepicker.MaterialDatePicker
import org.w3c.dom.Text
import whenweekly.frontend.databinding.ActivityCreatePlanBinding
import java.text.SimpleDateFormat
import java.util.*


class CreatePlanActivity : DrawerBaseActivity() {

    private lateinit var binding: ActivityCreatePlanBinding
    private var startDate: Long = 0L
    private var endDate: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCreatePlanBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setActivityTitle("Event Planner")

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
        Toast.makeText(this, "DATES:$startDate:$endDate", Toast.LENGTH_SHORT).show()
        if(binding.etEventName.text.isEmpty())
            errString.add("Please enter a name for the event and try again!")

        if(startDate == 0L || endDate == 0L)
            errString.add("Please select a date for the event and try again!")

        if (errString.isNotEmpty()) errString.forEach{
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            Thread.sleep(1_00)
            return
        }

        Consts.EventsConstants.EVENTS.add(EventModel(binding.etEventName.text.toString(), startDate, endDate))
        binding.etEventName.setText("")
        Toast.makeText(this, "Event added successfully!", Toast.LENGTH_SHORT).show()
        startDate = 0L
        endDate = 0L
        resetDateHolders(binding.startDateHolder, binding.endDateHolder)

    }

    private fun setDateHolders(startDateHolder: TextView?,endDateHolder: TextView?){
        fun formatDate(date:Long): String =
            SimpleDateFormat("yy.MM.dd").format(Date(date)).replace(".","/")
        endDateHolder!!.text = formatDate(endDate)
        startDateHolder!!.text = formatDate(startDate)
    }
    private fun resetDateHolders(startDateHolder: TextView?,endDateHolder: TextView?){
       val def = getString(R.string.defaultDateHolder)
        endDateHolder!!.text = def
        startDateHolder!!.text = def
    }

}