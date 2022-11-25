package whenweekly.frontend.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import whenweekly.frontend.api.Api
import whenweekly.frontend.databinding.ActivityRegisterBinding
import whenweekly.frontend.models.LocalUserModel

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRegisterBinding
    private var userInput = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val welcomeText = listOf("Welcome to WhenWeekly." ,
            "Please Enter Your Name...")
        lifecycleScope.launchWhenStarted {
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
            submit(binding.inputName.text.toString())
        }
    }

    /**
     * Displays text on the screen of the mobile device
     */
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

    /**
     * Submit the users name and add them to the database
     */
    private fun submit(name: String){
        if (name.isEmpty())
            Toast.makeText(this, "Please enter a valid name!", Toast.LENGTH_SHORT).show()
        else{
            val fragmentHolderIntent = Intent(this, FragmentHolderActivity::class.java)
            lifecycleScope.launch{
                val userResponse = Api.addUser(name)
                if (userResponse.data != null){
                    LocalUserModel(applicationContext).setUser(userResponse.data)
                    startActivity(fragmentHolderIntent)
                }
                else
                    Toast.makeText(this@RegisterActivity, "Error creating user!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}