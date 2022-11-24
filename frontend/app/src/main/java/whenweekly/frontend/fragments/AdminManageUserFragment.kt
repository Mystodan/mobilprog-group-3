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
import whenweekly.frontend.adapters.UserAdapter
import whenweekly.frontend.api.Api
import whenweekly.frontend.api.models.EventWithUsers
import whenweekly.frontend.app.Globals
import whenweekly.frontend.databinding.FragmentAdminManageUserBinding
import whenweekly.frontend.models.EventModel
import whenweekly.frontend.models.UserModel


class AdminManageUserFragment : Fragment() {
    private var userList = mutableListOf<UserModel>()       // List of users

    private var _binding : FragmentAdminManageUserBinding? = null
    private val binding get() = _binding!!
    private val adapter = UserAdapter(userList) { isChecked, position ->
        userList[position].checked = isChecked
    }

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
        var eventInformation = getEventModelFromParcel()

        _binding = FragmentAdminManageUserBinding.inflate(inflater, container, false)
        binding.rvUsers.adapter = adapter
        binding.rvUsers.addItemDecoration( // Adds separator between items
            DividerItemDecoration(binding.rvUsers.context, DividerItemDecoration.VERTICAL)
        )

        lifecycleScope.launchWhenStarted{
            getEventUsersFromAPI(eventInformation!!,deleteUser(eventInformation!!))
        }

        return binding.root
    }
    private fun getEventUsersFromAPI(eventInformation: EventModel, eventWithUsers: List<EventWithUsers>) {
        for(event in eventWithUsers) {
            if(event.event.inviteCode != eventInformation!!.invCode) continue
            for(user in event.users) {
                if(user.id == eventInformation.ownerId) continue
                userList.add(UserModel(user.name!!, false, user.id))
            }
        }
        adapter.updateData(userList)
    }

    private fun getEventModelFromParcel(): EventModel? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arguments?.getParcelable(Globals.Constants.LABEL_PARCEL_INFO, EventModel::class.java)
    } else
        arguments?.getParcelable(Globals.Constants.LABEL_PARCEL_INFO)

    private suspend fun deleteUser(eventInformation: EventModel):List<EventWithUsers>{
        val eventsResponse = Api.getEvents()
        if (eventsResponse.data != null) {
            binding.btnDelete.setOnClickListener {
                lifecycleScope.launch {
                    for(event in eventsResponse.data){
                        if(event.event.inviteCode != eventInformation!!.invCode) continue
                        for (user in userList){
                            if(!user.checked) continue
                            Api.kickUserFromEvent(event.event.id, user.id)
                            userList.remove(user)
                            adapter.updateData(userList)
                        }
                    }
                }
            }
            return eventsResponse.data
        }
        else{
            return emptyList()
        }
    }
}
