package whenweekly.frontend.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import kotlinx.coroutines.launch
import whenweekly.frontend.api.Api
import whenweekly.frontend.app.Globals

import whenweekly.frontend.databinding.FragmentEventDatesBinding
import whenweekly.frontend.models.EventModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities.Local


class DateReportFragment : Fragment() {
    private var _binding : FragmentEventDatesBinding? = null
    private val binding get() = _binding!!
    private var datesStart: List<Int> ? = null
    private var datesEnd: List<Int> ? = null
    private var unavailableDatesParent: MutableList<LocalDateTime> = mutableListOf()

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

        _binding = FragmentEventDatesBinding.inflate(inflater, container, false)

        binding.calendarView.selectionMode = MaterialCalendarView.SELECTION_MODE_MULTIPLE
        binding.calendarView.state().edit()
            .setMinimumDate(CalendarDay.from(datesStart!!.first(), datesStart!![1], datesStart!!.last()))
            .setMaximumDate(CalendarDay.from(datesEnd!!.first(), datesEnd!![1], datesEnd!!.last()))
            .setCalendarDisplayMode(CalendarMode.MONTHS)
            .commit()


        binding.btnReportDate.setOnClickListener {
            var allDates = mutableListOf<LocalDateTime>()
            lifecycleScope.launchWhenStarted {
                 Api.getAvailableDates(32).forEach { allDates.add(it) }
                val (unavailableDates, availableDates) = calculateAvailableDates(eventModel = eventInformation,allDates)
                val toastMSG =
                    if(unavailableDates.isEmpty()) "Please select dates first!"
                    else {
                        lifecycleScope.launch {
                            Api.updateAvailableDates(32, availableDates)
                        } ; "Dates reported successfully!" }
                Toast.makeText(context, toastMSG , Toast.LENGTH_SHORT).show()

            }


        }

        return binding.root
    }

    private fun toLocalDateTime(long: Long): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(long), TimeZone.getDefault().toZoneId());
    }


    private fun doesAvailableContainUnavailable(unavailableDates: List<LocalDateTime>,availableDates: List<LocalDateTime>):Boolean{
        availableDates.forEach{return (unavailableDates.contains(it))}
        return false
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
    private fun allDates(startDate: LocalDateTime, endDate: LocalDateTime): MutableList<LocalDateTime> {
        val numOfDaysBetween: Long = ChronoUnit.DAYS.between(startDate, endDate.plusDays(1))
        return Stream.iterate(startDate) { date -> date.plusDays(1) }.limit(numOfDaysBetween).collect(
            Collectors.toList())
    }

    /**
     * Removes the unavailable dates from all dates that are available
     *
     * @param allDates          - A mutable list of all LocalDates between startDate and endDate
     * @return                  - Returns allDates after it has removed all unavailable dates (returns available dates)
     */
    private fun calculateAvailableDates(eventModel: EventModel,allDates: MutableList<LocalDateTime>) : Pair<List<LocalDateTime>, List<LocalDateTime>> {
        val unavailableDates = allDates(toLocalDateTime(eventModel.startDate), toLocalDateTime(eventModel.endDate))
        unavailableDates.removeAll(allDates)
        val availableDates : MutableList<LocalDateTime> = allDates

        for(date in binding.calendarView.selectedDates) {
            val wrapDate = toLocalDateTime(date.date.toEpochDay()*86400000)
            if(!availableDates.contains(wrapDate) && unavailableDates.contains(wrapDate)){
                availableDates.add(wrapDate); unavailableDates.remove(wrapDate)
            }
        }
        return Pair(unavailableDates, availableDates)
    }

}