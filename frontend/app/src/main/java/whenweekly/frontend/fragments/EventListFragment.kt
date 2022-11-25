package whenweekly.frontend.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import kotlinx.coroutines.launch
import whenweekly.frontend.activities.FragmentHolderActivity
import whenweekly.frontend.adapters.EventAdapter
import whenweekly.frontend.api.Api
import whenweekly.frontend.app.Globals
import whenweekly.frontend.databinding.FragmentEventListBinding
import whenweekly.frontend.models.EventModel
import java.time.ZoneOffset

class EventListFragment : Fragment() {
    private var _binding: FragmentEventListBinding? = null
    private val binding get() = _binding!!
    private var adapter = EventAdapter(Globals.Lib.Events) {
        Globals.Utils.startEventActivityOfEvent(Globals.Lib.Events[it], requireActivity(),(activity as FragmentHolderActivity).getResult)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?:return
    }

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
        lifecycleScope.launch{
            val eventsResponse = Api.getEvents()
            if (eventsResponse.data != null) {
                val syncEventList = mutableListOf<EventModel>()
                syncEventList.addAll(eventsResponse.data.map {
                    EventModel(
                        it.event.name,
                        it.event.start_date.toEpochSecond(ZoneOffset.UTC) * 1000,
                        it.event.end_date.toEpochSecond(ZoneOffset.UTC) * 1000,
                        it.event.inviteCode,
                        it.event.id,
                        it.event.owner.id
                    )
                })
                Globals.Lib.Events = syncEventList
                adapter.updateData(Globals.Lib.Events)
            } else Toast.makeText(context,  eventsResponse.message, Toast.LENGTH_SHORT).show()
        }
        return binding.root
    }
}
