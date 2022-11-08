package whenweekly.frontend.activities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import whenweekly.frontend.R
import whenweekly.frontend.app.Globals
import whenweekly.frontend.databinding.ActivityEventBinding
import whenweekly.frontend.models.EventModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.stream.Collectors
import java.util.stream.Stream

class EventActivity : DrawerBaseActivity() {
    /**
     * Variable to be initiated later
     */
    private lateinit var binding: ActivityEventBinding      // Binding for the ActivityEvent
    private lateinit var clipboard: ClipboardManager

    private var datesStart: List<Int> ? = null
    private var datesEnd: List<Int> ? = null
    private var unavailableDates = mutableListOf<LocalDate>()

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
        reconfigureToolbar()

        // sets up clipboard manager
        clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        binding.copyCode.setOnClickListener { clipboard.setPrimaryClip(
            ClipData.newPlainText(Globals.Constants.LABEL_CLIP_INV,eventInformation.invCode))
            Toast.makeText(this, "Copied: ${eventInformation.invCode}", Toast.LENGTH_SHORT).show()
        }

        binding.calendarView.selectionMode = MaterialCalendarView.SELECTION_MODE_MULTIPLE
        binding.calendarView.state().edit()
            .setMinimumDate(CalendarDay.from(datesStart!!.first(), datesStart!![1], datesStart!!.last()))
            .setMaximumDate(CalendarDay.from(datesEnd!!.first(), datesEnd!![1], datesEnd!!.last()))
            .setCalendarDisplayMode(CalendarMode.MONTHS)
            .commit()

        binding.btnSelectDate.setOnClickListener {
            toggleCalendar(binding.calendarView.visibility)
        }

        binding.btnReportDate.setOnClickListener {
            val allDates = allDates(toLocalDate(eventInformation.startDate), toLocalDate(eventInformation.endDate))
            println("All dates: $allDates")

            val availableDates = calculateAvailableDates(allDates, unavailableDates)
            if(unavailableDates.isNotEmpty()) {
                Toast.makeText(this, "Dates reported successfully!", Toast.LENGTH_SHORT).show()
                toggleCalendar(binding.calendarView.visibility)
            }
            println("Unavailable dates: $unavailableDates")
            println("Available dates: $availableDates")
        }
    }

    /**
     * Intent used to receive data from a parcelable and set the content inside the layout XML to the data received
     */
    private fun getParcelableFromIntent():EventModel? {
        return intent.getParcelableExtra(Globals.Constants.LABEL_PARCEL_INFO)
    }

    /**
     *
     */
    private fun displayData(model: EventModel) {
        binding.eventTitle.text = model.eventName
        binding.eventStartDate.text = Globals.Utils.formatDate("yyyy.MM.dd", model.startDate)
        binding.eventEndDate.text = Globals.Utils.formatDate("yyyy.MM.dd", model.endDate)
        binding.code.text = model.invCode

        datesStart = timeAsInt(model.startDate)
        datesEnd = timeAsInt(model.endDate)
    }

    /**
     *
     */
    private fun reconfigureToolbar() {
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_arrow)
        toolbar.setNavigationIconColor(resources.getColor(R.color.white))
        toolbar.setNavigationOnClickListener{ finish() }
    }

    /**
     * Takes a Long and returns a list of the year, moth and day of that Long
     *
     * @param date  - Takes in date as Long
     * @return      - Returns date as a list of Ints (year, month, day)
     */
    private fun timeAsInt(date: Long) = listOf(
        Globals.Utils.formatDate("yyyy", date).toInt(),
        Globals.Utils.formatDate("MM", date).toInt(),
        Globals.Utils.formatDate("dd", date).toInt()
    )

    /**
     * Changes the visibility of the calendar on and off
     *
     * @param visibility    - The visibility of the calendar
     * @return              - Returns nothing, cancels the function
     */
    private fun toggleCalendar(visibility: Int) {
        if(visibility == View.INVISIBLE) {
            binding.calendarView.visibility = View.VISIBLE
            return
        }
        binding.calendarView.visibility = View.INVISIBLE
    }

    /**
     * Turns a Long into a java LocalDate and returns it
     *
     * @param long  - The Long to be returned as LocalDate
     * @return      - Turns a Long into a LocalDate and returns it
     */
    private fun toLocalDate(long: Long): LocalDate {
        return Instant.ofEpochMilli(long).atZone(ZoneId.systemDefault()).toLocalDate()
    }

    /**
     * Returns a mutable list of all the dates between a startDate and an endDate
     *
     * @param startDate     - The start date of the Event
     * @param endDate       - The end date of the Event
     * @return              - Returns all dates between startDate and endDate as a mutable list of LocalDates
     */
    private fun allDates(startDate: LocalDate, endDate: LocalDate): MutableList<LocalDate> {
        val numOfDaysBetween: Long = ChronoUnit.DAYS.between(startDate, endDate.plusDays(1))
        return Stream.iterate(startDate) { date -> date.plusDays(1) }.limit(numOfDaysBetween).collect(Collectors.toList())
    }

    /**
     * Removes the unavailable dates from all dates that are available
     *
     * @param alldates          - A mutable list of all LocalDates between startDate and endDate
     * @param unavailableDates  - Dates that are selected by the user and marked as unavailable
     * @return                  - Returns alldates after it has removed all unavailable dates
     */
    private fun calculateAvailableDates(alldates: MutableList<LocalDate>, unavailableDates: MutableList<LocalDate>): MutableList<LocalDate> {
        if(unavailableDates.isEmpty()) Toast.makeText(this, "Please select dates first!", Toast.LENGTH_SHORT).show()

        for(date in binding.calendarView.selectedDates) {
            unavailableDates.add(toLocalDate(date.date.toEpochDay()*86400000))
        }

        unavailableDates.forEach{ if(alldates.contains(it)) alldates.remove(it) }
        return alldates
    }
}