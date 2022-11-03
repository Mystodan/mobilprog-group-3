package whenweekly.frontend

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import whenweekly.frontend.databinding.ActivityLobbyBinding

class PlanListActivity : DrawerBaseActivity() {

    private lateinit var binding: ActivityLobbyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLobbyBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setActivityTitle("Event List")
    }
}