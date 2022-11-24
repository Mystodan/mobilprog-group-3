package whenweekly.frontend.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import whenweekly.frontend.api.Api
import whenweekly.frontend.app.Globals
import whenweekly.frontend.databinding.FragmentAdminDeleteBinding
import whenweekly.frontend.models.EventModel

class AdminDeleteFragment:Fragment() {
    private var _binding : FragmentAdminDeleteBinding? = null
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
        _binding = FragmentAdminDeleteBinding.inflate(inflater, container, false)
        setUpPrompt(eventInformation)

        binding.btnSubmitDelete.setOnClickListener {
            if(binding.inputEventName.text.toString() == eventInformation.eventName){
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
        binding.prompt.text = "Are you sure you want to delete \"${eventInformation.eventName}\"?"
        binding.inputEventName.hint = "Type \"${eventInformation.eventName}\" in order to confirm"
    }

    private fun getEventModelFromParcel(): EventModel? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arguments?.getParcelable(Globals.Constants.LABEL_PARCEL_INFO, EventModel::class.java)
    } else
        arguments?.getParcelable(Globals.Constants.LABEL_PARCEL_INFO)

}