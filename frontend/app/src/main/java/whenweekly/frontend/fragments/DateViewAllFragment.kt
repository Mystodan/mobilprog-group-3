package whenweekly.frontend.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import whenweekly.frontend.adapters.DateAdapter
import whenweekly.frontend.api.Api
import whenweekly.frontend.databinding.FragmentDateViewAllBinding
import whenweekly.frontend.models.DateModel

class DateViewAllFragment : Fragment() {

    private var availableDatesList = mutableListOf<DateModel>()       // List of available dates

    private var _binding : FragmentDateViewAllBinding? = null
    private val binding get() = _binding!!
    private val adapter = DateAdapter(availableDatesList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?:return
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDateViewAllBinding.inflate(inflater, container, false)
        binding.rvDates.adapter = adapter
        binding.rvDates.addItemDecoration( // Adds separator between items
            DividerItemDecoration(binding.rvDates.context, DividerItemDecoration.VERTICAL)
        )


        availableDatesList = mutableListOf(
            DateModel("2022-11-25"),
            DateModel("2022-11-26"),
            DateModel("2022-11-27"),
            DateModel("2022-11-28"),
            DateModel("2022-11-29")
        )

        adapter.updateData(availableDatesList)

        return binding.root
    }
}