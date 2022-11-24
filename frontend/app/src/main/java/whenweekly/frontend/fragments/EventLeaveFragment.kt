package whenweekly.frontend.fragments

import android.os.Build
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
import whenweekly.frontend.databinding.FragmentPromptBinding
import whenweekly.frontend.models.EventModel

class EventLeaveFragment:Fragment() {
    private var _binding : FragmentPromptBinding? = null
    private val binding get() = _binding!!

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
        val eventInformation = getEventModelFromParcel()!!
        _binding = FragmentPromptBinding.inflate(inflater, container, false)
        setUpPrompt(eventInformation)

        binding.btnSubmitPromt.setOnClickListener {
            if(binding.inputPrompt.text.toString() == Globals.Lib.CurrentUser?.name){
                lifecycleScope.launch {
                    Api.kickUserFromEvent(eventInformation.eventId, Globals.Lib.CurrentUser?.id!!)
                    Globals.Lib.Events.remove(eventInformation)
                    activity?.finish()
                }
            } else Toast.makeText(activity, "Invalid!", Toast.LENGTH_SHORT).show()
        }
        return binding.root
    }

    /**
     *
     */
    private fun setUpPrompt(eventInformation:EventModel){
        val output = Globals.Lib.CurrentUser?.name
        binding.prompt.text = "Are you (${output}) sure you want to leave \"${eventInformation.eventName}\"?"
        binding.inputPrompt.hint = "Type \"${output}\" in order to confirm"
        binding.btnSubmitPromt.text = "Leave Event"
    }

    /**
     *
     */
    private fun getEventModelFromParcel(): EventModel? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arguments?.getParcelable(Globals.Constants.LABEL_PARCEL_INFO, EventModel::class.java)
    } else
        arguments?.getParcelable(Globals.Constants.LABEL_PARCEL_INFO)
}