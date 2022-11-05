package whenweekly.frontend.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import whenweekly.frontend.models.EventModel
import whenweekly.frontend.R

/**
 * The adapter for an event
 *
 * @param dataList  - Takes the eventList as a parameter
 * @param onClick   - onClick function for the adapter
 */
class EventAdapter(private var dataList: List<EventModel>, val onClick:(Int)->Unit): RecyclerView.Adapter<EventAdapter.EventViewHolder>() {
    /**
     * The view holder for an event
     *
     * @param view  - The view for the function passed as a parameter
     */
    inner class EventViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val textView: TextView
        init {
            textView = view.findViewById(R.id.tv_text)
        }
    }

    /**
     * The on create view holder for the event
     *
     * @param parent    - The viewGroup parent of this function passed as a parameter
     * @param viewType  - The viewType of this function as an int passed as a parameter
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    /**
     * The on bind view holder for the event
     *
     * @param holder    - The holder of this function passed as a parameter
     * @param position  - Position of the event inside the dataList as an int
     */
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val item = dataList[holder.adapterPosition]
        holder.textView.text = item.eventName
        holder.textView.setOnClickListener {onClick(holder.adapterPosition)}
    }

    /**
     * Returns the size of the event model
     */
    override fun getItemCount(): Int {
        return  dataList.size
    }

    /**
     * Updates the data in the event model and notifies about the changes
     *
     * @param updatedDataList   - Takes the updated data list passed as a parameter
     */
    fun updateData(updatedDataList: List<EventModel>) {
        dataList = updatedDataList
        notifyDataSetChanged()
    }
}