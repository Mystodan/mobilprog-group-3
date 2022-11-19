package whenweekly.frontend.activities


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
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
        setDefaultFragment(Globals.Utils.startFragment, savedInstanceState)
        if (Globals.Lib.userId.isEmpty()) {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    /**
     *
     */
    private fun setDefaultFragment(target:Fragment, state: Bundle?){
        if (state == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.flContent, target)
                .commit()
        }
    }

}