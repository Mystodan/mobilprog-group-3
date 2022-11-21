package whenweekly.frontend.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import kotlinx.coroutines.launch
import whenweekly.frontend.activities.EventActivity
import whenweekly.frontend.adapters.EventAdapter
import whenweekly.frontend.api.Api
import whenweekly.frontend.app.Globals
import whenweekly.frontend.databinding.FragmentEventListBinding
import whenweekly.frontend.models.EventModel
import java.time.ZoneOffset


class EventListFragment : Fragment() {
    private var _binding : FragmentEventListBinding? = null
    private val binding get() = _binding!!
    private val eventList = Globals.Lib.Events
    private var adapter = EventAdapter(eventList) {changeActivity(eventList[it])}

    /**
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?:return
    }

    /**
     *
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {  // Inflate the layout for this fragment
        _binding = FragmentEventListBinding.inflate(inflater, container, false)
        binding.rvEvents.adapter = adapter
        binding.rvEvents.addItemDecoration( // Adds separator between items
            DividerItemDecoration(binding.rvEvents.context, DividerItemDecoration.VERTICAL)
        )

        adapter.updateData(Globals.Lib.Events)
        if (Globals.Lib.Events.isNotEmpty()) return binding.root
        lifecycleScope.launch{
            val events = Api.getEvents()

            Globals.Lib.Events.addAll(events.map {
                Globals.Utils.createEvent(
                    it.name,
                    it.start_date.toEpochSecond(ZoneOffset.UTC) * 1000,
                    it.end_date.toEpochSecond(ZoneOffset.UTC) * 1000, it.inviteCode)
            })
            adapter.updateData(Globals.Lib.Events)
        }

        return binding.root
    }

    /**
     *
     */
    private fun changeActivity(input: EventModel) {
        val intent = Intent(activity, EventActivity::class.java)
        intent.putExtra(Globals.Constants.LABEL_PARCEL_INFO, input)
        startActivity(intent)
    }
}
