package whenweekly.frontend.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import whenweekly.frontend.api.Api
import whenweekly.frontend.app.Globals

import whenweekly.frontend.databinding.FragmentEventJoinBinding
import java.time.ZoneOffset

class EventJoinFragment : Fragment() {
    private var _binding : FragmentEventJoinBinding? = null
    private val binding get() = _binding!!

    /**
     *
     */
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
     *
     */
    private fun joinEvent(inviteCode: String) {
        lifecycleScope.launch {
            val (event, error) = Api.joinEvent(inviteCode)
            if (event != null) {
                Globals.Lib.Events.add(Globals.Utils.createEvent(
                    event.event.name,
                    event.event.start_date.toEpochSecond(ZoneOffset.UTC) * 1000,
                    event.event.end_date.toEpochSecond(ZoneOffset.UTC) * 1000, event.event.inviteCode))
                Toast.makeText(context, "Event added", Toast.LENGTH_SHORT).show()
            } else if (error != null) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Unknown error occurred", Toast.LENGTH_SHORT).show()
            }
        }
    }
}