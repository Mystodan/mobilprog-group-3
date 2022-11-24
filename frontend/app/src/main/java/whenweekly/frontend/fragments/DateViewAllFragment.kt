package whenweekly.frontend.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import kotlinx.coroutines.launch
import whenweekly.frontend.adapters.DateAdapter
import whenweekly.frontend.api.Api
import whenweekly.frontend.databinding.FragmentEventShowAvailableDatesBinding
import whenweekly.frontend.models.DateModel
import java.time.LocalDateTime
import java.util.Date

class DateViewAllFragment : Fragment() {
    private var _binding : FragmentEventShowAvailableDatesBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: DateAdapter
    private var availableDatesList: MutableList<LocalDateTime> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?:return
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventShowAvailableDatesBinding.inflate(inflater, container, false)
        binding.rvDates.addItemDecoration( // Adds separator between items
            DividerItemDecoration(binding.rvDates.context, DividerItemDecoration.VERTICAL)
        )
        adapter = DateAdapter(availableDatesList)
        lifecycleScope.launch {
            Api.getAvailableDates(32).forEach{
                availableDatesList.add(it)
                adapter.updateData(availableDatesList)
            }
            println(availableDatesList)
        }

        binding.rvDates.adapter = adapter
        adapter.updateData(availableDatesList)

        return binding.root
    }
}