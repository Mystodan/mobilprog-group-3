package whenweekly.frontend.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import whenweekly.frontend.R
import whenweekly.frontend.databinding.ActivityFragmentHolderBinding

class FragmentHolderActivity : DrawerBaseActivity() {
    private lateinit var binding : ActivityFragmentHolderBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFragmentHolderBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}