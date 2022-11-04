package whenweekly.frontend

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import whenweekly.frontend.api.Api
import whenweekly.frontend.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Api().base {
            println(it)
        }

        binding.hamboog.setOnClickListener {

        }

    }
}