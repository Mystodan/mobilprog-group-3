package whenweekly.frontend.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import io.ktor.http.*
import kotlinx.coroutines.launch
import whenweekly.frontend.R
import whenweekly.frontend.api.Api
import whenweekly.frontend.app.Globals
import whenweekly.frontend.databinding.ActivityFragmentHolderBinding

class FragmentHolderActivity : DrawerBaseActivity() {
    private lateinit var binding : ActivityFragmentHolderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFragmentHolderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setDefaultFragment(Globals.Utils.startFragment, savedInstanceState, R.id.flContent)
        println(Globals.Lib.CurrentUser?.uuidToString())

        lifecycleScope.launch {
        if (Globals.Lib.localUUID.isEmpty()) {
            startActivity(Intent(this@FragmentHolderActivity, RegisterActivity::class.java))
            finish()
        } else {
                val userResponse = Api.getUser()
                if (userResponse.data != null) {
                    Globals.Lib.CurrentUser = userResponse.data
                    Globals.Lib.localUUID = userResponse.data.uuidToString()
                }
                else if (userResponse.status == HttpStatusCode.NotFound){
                    println("User not found, database probably reset. Registering new user")
                    startActivity(Intent(this@FragmentHolderActivity, RegisterActivity::class.java))
                    finish()
                }
                else {
                    println("Error getting user: " + userResponse.message + " " + userResponse.status)
                }
            }
        }

    }

    val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        loadFragment(Globals.Utils.startFragment::class.java)
    }
}















