package whenweekly.frontend

import android.content.Intent
import android.os.Bundle
import whenweekly.frontend.databinding.ActivityLobbyBinding

class PlanListActivity : DrawerBaseActivity() {
    private val eventList = Consts.EventsConstants.EVENTS
    private lateinit var binding: ActivityLobbyBinding
    private var adapter = EventAdapter(eventList) {changeActivity(eventList[it])}


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLobbyBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setActivityTitle("Event List")

        binding.rvEvents.adapter = adapter

        adapter.updateData(eventList)
    }

    private fun changeActivity(input: EventModel) {
        val intent = Intent(this, EventActivity::class.java)
        intent.putExtra(Consts.EventsConstants.information, input)
        startActivity(intent)
    }
}