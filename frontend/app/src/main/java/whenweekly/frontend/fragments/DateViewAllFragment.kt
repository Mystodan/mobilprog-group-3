package whenweekly.frontend.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import kotlinx.coroutines.launch
import whenweekly.frontend.adapters.DateAdapter
import whenweekly.frontend.api.Api
import whenweekly.frontend.app.Globals
import whenweekly.frontend.databinding.FragmentDateViewAllBinding
import whenweekly.frontend.models.EventModel
import java.time.LocalDateTime

class DateViewAllFragment : Fragment() {
    private var _binding : FragmentDateViewAllBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: DateAdapter
    private var availableDatesList: MutableList<LocalDateTime> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?:return
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val eventInformation = getEventModelFromParcel()
        _binding = FragmentDateViewAllBinding.inflate(inflater, container, false)
        binding.rvDates.addItemDecoration( // Adds separator between items
            DividerItemDecoration(binding.rvDates.context, DividerItemDecoration.VERTICAL)
        )
        adapter = DateAdapter(availableDatesList)
        lifecycleScope.launch {
            Api.getAvailableDates(eventInformation?.eventId!!).data?.forEach{
                availableDatesList.add(it)
                adapter.updateData(availableDatesList.distinct())
            }
            println(availableDatesList)
        }

        binding.rvDates.adapter = adapter
        adapter.updateData(availableDatesList)

        return binding.root
    }

    /**
     * Gets the EventModel of the current Event from the EventActivity
     */
    private fun getEventModelFromParcel(): EventModel? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arguments?.getParcelable(Globals.Constants.LABEL_PARCEL_INFO, EventModel::class.java)
    } else arguments?.getParcelable(Globals.Constants.LABEL_PARCEL_INFO)
}