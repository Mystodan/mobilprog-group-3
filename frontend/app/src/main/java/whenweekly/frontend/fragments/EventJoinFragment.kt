package whenweekly.frontend.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import whenweekly.frontend.activities.FragmentHolderActivity
import whenweekly.frontend.api.Api
import whenweekly.frontend.app.Globals
import whenweekly.frontend.databinding.FragmentEventJoinBinding
import whenweekly.frontend.models.EventModel
import java.time.ZoneOffset

class EventJoinFragment : Fragment() {
    private var _binding: FragmentEventJoinBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {  // Inflate the layout for this fragment
        _binding = FragmentEventJoinBinding.inflate(inflater, container, false)

        binding.joinEvent.setOnClickListener {
            joinEvent(binding.inputCode.text.toString())
        }

        return binding.root
    }

    /**
     * Function that the user can use to join an event
     */
    private fun joinEvent(inviteCode: String) {
        lifecycleScope.launch {
            val eventResponse = Api.joinEvent(inviteCode)
            if (eventResponse.data != null) {
                val event = eventResponse.data
                val localEvent = EventModel(
                    event.event.name,
                    event.event.start_date.toEpochSecond(ZoneOffset.UTC) * 1000,
                    event.event.end_date.toEpochSecond(ZoneOffset.UTC) * 1000,
                    event.event.inviteCode,
                    event.event.id,
                    event.event.owner.id)
                Globals.Lib.Events.add(localEvent)
                Toast.makeText(context, "Event added", Toast.LENGTH_SHORT).show()
                Globals.Utils.startEventActivityOfEvent(localEvent, requireActivity(), (activity as FragmentHolderActivity).getResult)
            } else Toast.makeText(context, eventResponse.message, Toast.LENGTH_SHORT).show()
        }
    }
}