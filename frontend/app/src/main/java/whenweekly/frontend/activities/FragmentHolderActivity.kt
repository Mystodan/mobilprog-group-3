package whenweekly.frontend.activities


import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import whenweekly.frontend.R
import whenweekly.frontend.app.Globals
import whenweekly.frontend.databinding.ActivityFragmentHolderBinding


class FragmentHolderActivity : DrawerBaseActivity() {
    private lateinit var binding : ActivityFragmentHolderBinding
    /**
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFragmentHolderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setDefaultFragment(Globals.Utils.startFragment, savedInstanceState, R.id.flContent)
        println(Globals.Lib.CurrentUser?.uuidToString())
        if (Globals.Lib.CurrentUser == null || Globals.Lib.CurrentUser?.uuidToString()==null) {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

    }
    val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        loadFragment(Globals.Utils.startFragment::class.java)
    }





}















