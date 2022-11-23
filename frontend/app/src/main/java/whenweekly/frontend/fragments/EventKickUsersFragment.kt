package whenweekly.frontend.fragments

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
import whenweekly.frontend.app.Globals
import whenweekly.frontend.databinding.FragmentEventDeleteUsersBinding
import whenweekly.frontend.models.UserModel
import java.time.ZoneOffset


class EventKickUsersFragment : Fragment() {
    private var userList = mutableListOf<UserModel>()       // List of users

    private var _binding : FragmentEventDeleteUsersBinding? = null
    private val binding get() = _binding!!
    val adapter = UserAdapter(userList) {isChecked, position ->
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
        _binding = FragmentEventDeleteUsersBinding.inflate(inflater, container, false)
        binding.rvUsers.adapter = adapter
        binding.rvUsers.addItemDecoration( // Adds separator between items
            DividerItemDecoration(binding.rvUsers.context, DividerItemDecoration.VERTICAL)
        )

        userList = mutableListOf<UserModel>(
            UserModel("user1", false),
            UserModel("user2", true)
        )

        adapter.updateData(userList)

        /**
         * Function that checks if a user is checked and deletes it if it is checked
         */
        binding.btnDelete.setOnClickListener {
            val listIterator = userList.iterator()
            while(listIterator.hasNext()) {
                val groceryItem = listIterator.next()
                if(groceryItem.checked) {
                    listIterator.remove()
                }
            }
            adapter.updateData(userList)

            lifecycleScope.launch {
                var eventWithUsers = Api.getEvents()
                for(event in eventWithUsers) {
                    println(event.users)
                }
            }
        }

        return binding.root
    }
}
