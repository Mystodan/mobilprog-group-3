package whenweekly.frontend.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import whenweekly.frontend.api.Api
import whenweekly.frontend.app.Globals
import whenweekly.frontend.databinding.FragmentPromptBinding
import whenweekly.frontend.models.EventModel

class AdminDeleteFragment:Fragment() {
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
        var eventInformation = getEventModelFromParcel()!!
        _binding = FragmentPromptBinding.inflate(inflater, container, false)
        setUpPrompt(eventInformation)
        binding.btnSubmitPromt.setOnClickListener {
            if(binding.inputPrompt.text.toString() == eventInformation.eventName){
                lifecycleScope.launch {
                    Api.deleteEvent(eventInformation.eventId)
                    Globals.Lib.Events.remove(eventInformation)
                    activity?.finish()
                }
            }
        }
        return binding.root
    }
    private fun setUpPrompt(eventInformation:EventModel){
        var event = eventInformation.eventName
        binding.prompt.text = "Are you sure you want to delete \"${event}\"?"
        binding.inputPrompt.hint = "Type \"${event}\" in order to confirm"
        binding.btnSubmitPromt.text = "Delete ${event}"
    }

    private fun getEventModelFromParcel(): EventModel? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arguments?.getParcelable(Globals.Constants.LABEL_PARCEL_INFO, EventModel::class.java)
    } else
        arguments?.getParcelable(Globals.Constants.LABEL_PARCEL_INFO)

}