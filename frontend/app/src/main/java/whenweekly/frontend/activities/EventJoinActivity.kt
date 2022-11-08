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

        Globals.Lib.Events.forEach{ // server sided holder of events
            if(strInn == it.invCode && !Globals.Lib.Events.contains(it)){ // checks if local contains serversided event
                Globals.Lib.Events.add(it) // add if not
                return // return if added
            }// if not then send error
            Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show()
        }
    }
}