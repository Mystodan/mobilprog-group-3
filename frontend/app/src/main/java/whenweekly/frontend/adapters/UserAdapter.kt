package whenweekly.frontend.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import whenweekly.frontend.R
import whenweekly.frontend.models.UserModel

/**
 * The adapter for a user
 *
 * @param dataList  - Takes the userList as a parameter
 * @param onClick   - onClick function for the adapter
 */
class UserAdapter(private var dataList: List<UserModel>, val onClick:(Boolean, Int)->Unit): RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    /**
     * The view holder for an user
     *
     * @param view  - The view for the function passed as a parameter
     */
    inner class UserViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val textView: TextView
        val checkBox: CheckBox
        init {
            textView = view.findViewById(R.id.tv_userName)
            checkBox = view.findViewById(R.id.checkBox)
        }
    }

    /**
     * The on create view holder for the user
     *
     * @param parent    - The viewGroup parent of this function passed as a parameter
     * @param viewType  - The viewType of this function as an int passed as a parameter
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    /**
     * The on bind view holder for the user
     *
     * @param holder    - The holder of this function passed as a parameter
     * @param position  - Position of the user inside the dataList as an int
     */
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val item = dataList[holder.adapterPosition]
        holder.textView.text = item.userName
        holder.checkBox.isChecked = item.checked
        holder.checkBox.setOnCheckedChangeListener {compoundButton, b ->
            onClick(b, holder.adapterPosition)
        }
    }

    /**
     * Returns the size of the user model
     */
    override fun getItemCount(): Int {
        return  dataList.size
    }

    /**
     * Updates the data in the user model and notifies about the changes
     *
     * @param updatedDataList   - Takes the updated data list passed as a parameter
     */
    fun updateData(updatedDataList: List<UserModel>) {
        dataList = updatedDataList
        notifyDataSetChanged()
    }
}