package whenweekly.frontend.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import whenweekly.frontend.databinding.ActivityRegisterBinding
import whenweekly.frontend.models.LocalUserModel

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRegisterBinding
    var userInput = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var welcomeText = listOf("Welcome to WhenWeekly." ,
            "Please Enter Your Name...")

        GlobalScope.launch {
            showText(welcomeText[0], binding.welcomeText, null)
            showText(welcomeText[1], null, binding.inputName)
        }
        binding.inputName.addTextChangedListener {
            if (!userInput){
                binding.welcomeText.text = welcomeText[0]
                binding.inputName.hint = welcomeText[1]
                userInput = true
            }
        }
        binding.submitName.setOnClickListener {
            submit()
        }
    }



    private suspend fun showText(text: String, textView: TextView?, editView: EditText?){
        delay(100L)
        var textHolder = ""
        text.forEach{
            if (!userInput) {
                textHolder += it
                if (textView != null) textView.text = textHolder
                else if (editView != null) editView.hint = textHolder
                delay(90L)
            }
        }
    }
    private fun submit(){
        if (binding.inputName.text.isEmpty())
            Toast.makeText(this, "Please enter a valid name!", Toast.LENGTH_SHORT).show()
        else{
            startActivity(Intent(this, FragmentHolderActivity::class.java))
            LocalUserModel(applicationContext).genUUID()
            LocalUserModel(applicationContext).setGlobalUserID()
            finish()

        }
    }
}