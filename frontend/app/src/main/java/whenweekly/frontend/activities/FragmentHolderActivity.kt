package whenweekly.frontend.activities


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import whenweekly.frontend.R
import whenweekly.frontend.app.Globals
import whenweekly.frontend.databinding.ActivityFragmentHolderBinding


class FragmentHolderActivity : DrawerBaseActivity() {
    private lateinit var binding : ActivityFragmentHolderBinding
    protected var eventOpen = false
    /**
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFragmentHolderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setDefaultFragment(Globals.Utils.startFragment, savedInstanceState, R.id.flContent)
        if (Globals.Lib.LocalUUID.isEmpty()) {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

    }





}















