package whenweekly.frontend.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch
import whenweekly.frontend.app.Globals
import whenweekly.frontend.R
import whenweekly.frontend.activities.FragmentHolderActivity
import whenweekly.frontend.api.Api
import whenweekly.frontend.databinding.FragmentEventCreateBinding
import whenweekly.frontend.models.EventModel
import java.time.LocalDateTime
import java.time.ZoneOffset

class EventCreateActivity : Fragment() {

    private var _binding : FragmentEventCreateBinding? = null
    private val binding get() = _binding!!
    private var startDate: Long = 0L
    private var endDate: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {  // Inflate the layout for this fragment
        _binding = FragmentEventCreateBinding.inflate(inflater, container, false)
        super.onCreate(savedInstanceState)
        // gets all inv codes

        // sets the functionality for picking dates
        binding.selectDates.setOnClickListener { showDataRangePicker() }
        // sets the functionality for creating an event
        binding.btnCreateEvent.setOnClickListener { createEventPlan() }

        return binding.root
    }

    /**
     *
     */
    private fun showDataRangePicker() {
        var setupDatePicker = MaterialDatePicker
            .Builder.dateRangePicker()
            .setTitleText("Select Date")
        if(startDate != 0L || endDate != 0L) setupDatePicker = setupDatePicker.setSelection(Pair(startDate,endDate))
        val dateRangePicker = setupDatePicker.build()

        dateRangePicker.show(parentFragmentManager, "date_range_picker")
        dateRangePicker.addOnPositiveButtonClickListener { dateSelected ->
            startDate = dateSelected.first
            endDate = dateSelected.second
            setDateHolders(binding.startDateHolder, binding.endDateHolder)
        }
    }

    private fun Long.toLocalDateTime(): LocalDateTime = LocalDateTime.ofEpochSecond(this / 1000, 0, ZoneOffset.UTC)
    /**
     *
     */
    private fun createEventPlan() {
        val errString = mutableListOf<String>()
        if(binding.etEventName.text.isEmpty())
            errString.add("Please enter a name for the event and try again!")

        if(startDate == 0L || endDate == 0L)
            errString.add("Please select a date for the event and try again!")

        if (errString.isNotEmpty()){ errString.forEach{
            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
            Thread.sleep(1_00)
        }; return }

        LocalDateTime.ofEpochSecond(startDate, 0, ZoneOffset.UTC)
        lifecycleScope.launch {
            val eventName = binding.etEventName.text.toString()
            val newEvent = Api.addEvent(eventName, "filler description", startDate.toLocalDateTime(), endDate.toLocalDateTime())
            var localEvent : EventModel?=null
            if(newEvent != null ) {
                localEvent = EventModel(eventName, startDate, endDate, newEvent.event.inviteCode,newEvent.event.id)
                Globals.Lib.Events.add(localEvent)
                println(Globals.Lib.CurrentUser?.id)
                Toast.makeText(activity, "Event created!", Toast.LENGTH_SHORT).show()
                println("activity:${requireActivity()} \nContext:${requireContext()}")
                Globals.Utils.startEventActivityOfEvent(localEvent, requireActivity(), (activity as FragmentHolderActivity).getResult)
            } else {
                Toast.makeText(activity, "Error creating event!", Toast.LENGTH_SHORT).show()
            }
            resetDateHolders(binding.startDateHolder, binding.endDateHolder,binding.etEventName)

        }
    }

    /**
     *
     */
    private fun setDateHolders(startDateHolder: TextView?,endDateHolder: TextView?) {
        fun formatDate(date:Long): String = Globals.Utils.formatDate("yy.MM.dd", date).replace(".","/")
        endDateHolder!!.text = formatDate(endDate)
        startDateHolder!!.text = formatDate(startDate)
    }

    /**
     *
     */
    private fun resetDateHolders(startDateHolder: TextView?,endDateHolder: TextView?, editTextHolder:EditText?) {
        val def = getString(R.string.defaultDateHolder)
        editTextHolder!!.setText(def)
        endDateHolder!!.text = def
        startDateHolder!!.text = def
        startDate = 0L
        endDate = 0L
    }
}