package whenweekly.frontend.activities

import android.os.Bundle
import android.widget.Toast
import whenweekly.frontend.app.Globals
import whenweekly.frontend.databinding.ActivityEventJoinBinding

class EventJoinActivity : DrawerBaseActivity() {
    private lateinit var binding: ActivityEventJoinBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityEventJoinBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setActivityTitle("Event Invite Code")

        //binding.joinEvent// btn
        //binding.inputCode// edittext
        binding.joinEvent.setOnClickListener{
            tryAddEvent(binding.inputCode.text.toString())
        }

    }

    private fun tryAddEvent(strInn:String){

        Globals.Constants.MOCKED_EXTERNAL_EVENTS.forEach{
            if(strInn == it.invCode && !Globals.Constants.EVENTS.contains(it)){
                Globals.Constants.EVENTS.add(it)
                return
            }
            Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show()
        }



    }

}