package whenweekly.frontend

import android.os.Bundle
import whenweekly.frontend.databinding.ActivityJoinPlanBinding

class JoinPlanActivity : DrawerBaseActivity() {
    private lateinit var binding: ActivityJoinPlanBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityJoinPlanBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setActivityTitle("Event Invite Code")
    }
}