package whenweekly.frontend

import android.os.Bundle
import whenweekly.frontend.databinding.ActivityCreatePlanBinding


class CreatePlanActivity : DrawerBaseActivity() {

    private lateinit var binding: ActivityCreatePlanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCreatePlanBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        
        setActivityTitle("Event Planner")
    }
}