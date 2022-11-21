package whenweekly.frontend.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import whenweekly.frontend.app.Globals

import whenweekly.frontend.databinding.FragmentEventJoinBinding

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
            tryAddEvent(binding.inputCode.text.toString())
        }

        return binding.root
    }

    /**
     *
     */
    private fun tryAddEvent(strInn: String) {

        lifecycleScope.launch {
        }

        Globals.Lib.Events.forEach{ // server sided holder of events
            if(strInn == it.invCode && !Globals.Lib.Events.contains(it)) { // checks if local contains serverside event
                Globals.Lib.Events.add(it) // add if not
                return // return if added
            } // if not then send error
            Toast.makeText(activity, "Invalid code", Toast.LENGTH_SHORT).show()
        }
    }
}