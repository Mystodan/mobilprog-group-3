package whenweekly.frontend.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import whenweekly.frontend.R
import whenweekly.frontend.models.DateModel

/**
 * The adapter for an available date
 *
 * @param dataList  - Takes the availableDateList as a parameter
 */
class DateAdapter(private var dataList: List<DateModel>): RecyclerView.Adapter<DateAdapter.DateViewHolder>() {
    /**
     * The view holder for an available date
     *
     * @param view  - The view for the function passed as a parameter
     */
    inner class DateViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val textView: TextView
        init {
            textView = view.findViewById(R.id.tv_date)
        }
    }

    /**
     * The on create view holder for the available date
     *
     * @param parent    - The viewGroup parent of this function passed as a parameter
     * @param viewType  - The viewType of this function as an int passed as a parameter
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_date, parent, false)
        return DateViewHolder(view)
    }

    /**
     * The on bind view holder for the available date
     *
     * @param holder    - The holder of this function passed as a parameter
     */
    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val item = dataList[holder.adapterPosition]
        holder.textView.text = item.availableDate
    }

    /**
     * Returns the size of the date model
     */
    override fun getItemCount(): Int {
        return  dataList.size
    }

    /**
     * Updates the data in the date model and notifies about the changes
     *
     * @param updatedDataList   - Takes the updated data list passed as a parameter
     */
    fun updateData(updatedDataList: List<DateModel>) {
        dataList = updatedDataList
        notifyDataSetChanged()
    }
}