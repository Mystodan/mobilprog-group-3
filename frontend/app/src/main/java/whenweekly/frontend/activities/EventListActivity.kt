package whenweekly.frontend.activities

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import whenweekly.frontend.adapters.EventAdapter
import whenweekly.frontend.models.EventModel
import whenweekly.frontend.app.Globals
import whenweekly.frontend.databinding.ActivityEventListBinding

class EventListActivity : DrawerBaseActivity() {
    private val eventList = Globals.Constants.EVENTS
    private lateinit var binding: ActivityEventListBinding
    private var adapter = EventAdapter(eventList) {changeActivity(eventList[it])}


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityEventListBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setActivityTitle("Event List")

        binding.rvEvents.addItemDecoration( // Adds separator between items
            DividerItemDecoration(binding.rvEvents.context,DividerItemDecoration.VERTICAL)
        )

        binding.rvEvents.adapter = adapter

        adapter.updateData(eventList)
    }

    private fun changeActivity(input: EventModel) {
        val intent = Intent(this, EventActivity::class.java)
        intent.putExtra(Globals.Constants.LABEL_PARCEL_INFO, input)
        startActivity(intent)
    }
}