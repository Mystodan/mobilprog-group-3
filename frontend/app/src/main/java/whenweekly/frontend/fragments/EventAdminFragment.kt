package whenweekly.frontend.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import kotlinx.coroutines.launch
import whenweekly.frontend.R
import whenweekly.frontend.activities.EventActivity
import whenweekly.frontend.adapters.UserAdapter
import whenweekly.frontend.api.Api
import whenweekly.frontend.api.models.EventWithUsers
import whenweekly.frontend.app.Globals
import whenweekly.frontend.databinding.FragmentAdminPanelBinding
import whenweekly.frontend.models.EventModel
import whenweekly.frontend.models.UserModel


class EventAdminFragment : Fragment() {
    enum class ButtonPanel{
        Users, Edit, Delete
    }
    private var buttonPanel: ButtonPanel = ButtonPanel.Users
    private var _binding: FragmentAdminPanelBinding? = null
    private val binding get() = _binding!!
    private var currFragment: Fragment = DateViewAllFragment()

    /**
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments ?: return
    }

    /**
     *
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {  // Inflate the layout for this fragment
        var eventInformation = getEventModelFromParcel()
        _binding = FragmentAdminPanelBinding.inflate(inflater, container, false)
        manageAdminState(eventInformation!!)

        return binding.root
    }
    private fun getEventModelFromParcel(): EventModel? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arguments?.getParcelable(Globals.Constants.LABEL_PARCEL_INFO, EventModel::class.java)
    } else
        arguments?.getParcelable(Globals.Constants.LABEL_PARCEL_INFO)

    private fun manageAdminState(model:EventModel){
        binding.btnManageUser.setOnClickListener {
            buttonPanel = ButtonPanel.Users
            changePanelView(buttonPanel, model)
        }
        binding.btnEventEdit.setOnClickListener {
            buttonPanel = ButtonPanel.Edit
            changePanelView(buttonPanel, model)
        }
        binding.btnEventDelete.setOnClickListener {
            buttonPanel = ButtonPanel.Delete
            changePanelView(buttonPanel, model)
        }
    }
    private fun changePanelView(menuItem: ButtonPanel, parcel: EventModel){
        val componentClass: Class<*> = when(menuItem){
            ButtonPanel.Users -> AdminManageUserFragment::class.java
            ButtonPanel.Edit -> DateViewAllFragment::class.java
            else -> AdminDeleteFragment::class.java
        }

        loadFragment(componentClass, parcel)
    }
    private fun loadFragment(fragmentClass:Class<*>?, parcel: EventModel) {
        var fragment: Fragment? = null
        try {
            fragment = fragmentClass?.newInstance() as Fragment
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (fragment != null && currFragment != fragment) {
            println(fragment)
            val eventBundle = Bundle()
            eventBundle.putParcelable(Globals.Constants.LABEL_PARCEL_INFO,parcel)
            fragment.arguments = eventBundle
            parentFragmentManager.beginTransaction().replace(R.id.adminFragmentHolder, fragment).commit()
            currFragment = fragment
        }
    }
}





