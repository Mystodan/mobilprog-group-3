package whenweekly.frontend.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import whenweekly.frontend.app.Globals

import whenweekly.frontend.databinding.FragmentEventDatesBinding
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.stream.Collectors
import java.util.stream.Stream

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"
private const val ARG_PARAM4 = "param4"

class EventDatesFragment : Fragment() {
    private var _binding : FragmentEventDatesBinding? = null
    private val binding get() = _binding!!
    private var datesStart: List<Int> ? = null
    private var datesEnd: List<Int> ? = null
    private var fragmentEventName: String? = null
    private var fragmentStartDate: Long? = null
    private var fragmentEndDate: Long? = null
    private var fragmentInvCode: String? = null

    /**
     *
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {  // Inflate the layout for this fragment
        arguments?.let {
            fragmentEventName = it.getString(ARG_PARAM1)
            fragmentStartDate = it.getLong(ARG_PARAM2)
            fragmentEndDate = it.getLong(ARG_PARAM3)
            fragmentInvCode = it.getString(ARG_PARAM4)
        }

        datesStart = timeAsInt(fragmentStartDate!!)
        datesEnd = timeAsInt(fragmentEndDate!!)

        _binding = FragmentEventDatesBinding.inflate(inflater, container, false)

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
            val allDates = allDates(toLocalDate(fragmentStartDate!!), toLocalDate(fragmentEndDate!!))
            println("All dates: $allDates")

            val (availableDates, unavailableDates) = calculateAvailableDates(allDates)
            if(unavailableDates.isNotEmpty()) {
                Toast.makeText(context, "Dates reported successfully!", Toast.LENGTH_SHORT).show()
                toggleCalendar(binding.calendarView.visibility)
            } else Toast.makeText(context, "Please select dates first!", Toast.LENGTH_SHORT).show()
            println("Unavailable dates: $unavailableDates")
            println("Available dates: $availableDates")
        }

        return binding.root
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
        return Stream.iterate(startDate) { date -> date.plusDays(1) }.limit(numOfDaysBetween).collect(
            Collectors.toList())
    }

    /**
     * Removes the unavailable dates from all dates that are available
     *
     * @param allDates          - A mutable list of all LocalDates between startDate and endDate
     * @param unavailableDates  - Dates that are selected by the user and marked as unavailable
     * @return                  - Returns alldates after it has removed all unavailable dates
     */
    private fun calculateAvailableDates(allDates: MutableList<LocalDate>): Pair<MutableList<LocalDate>,MutableList<LocalDate>> {
        var availableDates : MutableList<LocalDate> = allDates
        var unavailableDates : MutableList<LocalDate> = mutableListOf()
        for(date in binding.calendarView.selectedDates) {
            unavailableDates.add(toLocalDate(date.date.toEpochDay()*86400000))
        }
        unavailableDates.forEach{ if(availableDates.contains(it)) availableDates.remove(it) }
        return Pair(availableDates, unavailableDates)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: Long, param3: Long, param4: String) =
            EventDatesFragment().apply {
                arguments = Bundle().apply {
                    putString("eventName", param1)
                    putLong("eventStart", param2)
                    putLong("eventEnd", param3)
                    putString("invCode", param4)
                }
            }
    }
}