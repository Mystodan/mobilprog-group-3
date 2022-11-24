package whenweekly.frontend.fragments

import android.os.Build
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
import whenweekly.frontend.databinding.FragmentDateReportBinding
import whenweekly.frontend.models.EventModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.stream.Collectors
import java.util.stream.Stream


class DateReportFragment : Fragment() {
    private var _binding : FragmentDateReportBinding? = null
    private val binding get() = _binding!!
    private var datesStart: List<Int> ? = null
    private var datesEnd: List<Int> ? = null
    private var unavailableDatesParent: MutableList<LocalDate> = mutableListOf()

    /**
     *
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {  // Inflate the layout for this fragment
       val eventInformation = getEventModelFromParcel()


        datesStart = timeAsInt(eventInformation?.startDate!!)
        datesEnd = timeAsInt(eventInformation.endDate)

        _binding = FragmentDateReportBinding.inflate(inflater, container, false)

        binding.calendarView.selectionMode = MaterialCalendarView.SELECTION_MODE_MULTIPLE
        binding.calendarView.state().edit()
            .setMinimumDate(CalendarDay.from(datesStart!!.first(), datesStart!![1], datesStart!!.last()))
            .setMaximumDate(CalendarDay.from(datesEnd!!.first(), datesEnd!![1], datesEnd!!.last()))
            .setCalendarDisplayMode(CalendarMode.MONTHS)
            .commit()


        binding.btnReportDate.setOnClickListener {
            val allDates = allDates(toLocalDate(eventInformation.startDate), toLocalDate(eventInformation.endDate))
            println("All dates: $allDates")

            println(localDateToString(allDates))

            val (unavailableDates) = calculateAvailableDates(allDates)
            val toastMSG =
                if(unavailableDates.isEmpty()) "Please select dates first!"
                else if (doesAvailableContainUnavailable(unavailableDatesParent, unavailableDates)) "${getUnavailableDatesAsDays()} of the selected dates are unavailable"
                else { unavailableDates.forEach{if(!unavailableDatesParent.contains(it))unavailableDatesParent.add(it)}; "Dates reported successfully!"}
            Toast.makeText(context, toastMSG , Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    private fun doesAvailableContainUnavailable(unavailableDates: MutableList<LocalDate>,availableDates: MutableList<LocalDate>):Boolean{
        availableDates.forEach{return (unavailableDates.contains(it))}
        return false
    }

    private fun localDateToString(datesList: MutableList<LocalDate>) : MutableList<String> {
        val stringDates = mutableListOf<String>()
        calculateAvailableDates(datesList).first.forEach{ stringDates.add("\"$it\"") }
        return stringDates
    }

    private fun getEventModelFromParcel():EventModel? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arguments?.getParcelable(Globals.Constants.LABEL_PARCEL_INFO, EventModel::class.java)
    } else
        arguments?.getParcelable(Globals.Constants.LABEL_PARCEL_INFO)

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
    private fun getUnavailableDatesAsDays():MutableList<Int> {
        val retList = mutableListOf<Int>()
        unavailableDatesParent.forEach{retList.add(it.dayOfMonth) }
        return retList
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
     * @return                  - Returns alldates after it has removed all unavailable dates
     */
    private fun calculateAvailableDates(allDates: MutableList<LocalDate>): Pair<MutableList<LocalDate>,MutableList<LocalDate>> {
        val availableDates : MutableList<LocalDate> = allDates
        val unavailableDates : MutableList<LocalDate> = mutableListOf()
        for(date in binding.calendarView.selectedDates) {
            unavailableDates.add(toLocalDate(date.date.toEpochDay()*86400000))
        }
        unavailableDates.forEach{ if(availableDates.contains(it)) availableDates.remove(it) }
        return Pair(availableDates, unavailableDates)
    }

}