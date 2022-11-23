package whenweekly.frontend.models

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import whenweekly.frontend.app.Globals
import java.util.*

/**
 * The data model for a single user using parcelables to "send" information to a receiver that can use that data to put into their layout XML
 */
data class UserModel(
    var userName: String,
    var checked: Boolean
) : Parcelable {
    /**
     * Constructor for the parcels that sets values for the UserModel parcel
     *
     * @param parcel    - Takes a parcel as a parameter
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readBoolean()
    )

    /**
     * Function that writes values to the parcel
     *
     * @param parcel    - Takes a parcel as a parameter
     * @param flags     - Various flags passed as an int
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userName)
        parcel.writeBoolean(checked)
    }

    /**
     * Function for describing contents inside the parcelable
     */
    override fun describeContents(): Int {
        return 0
    }

    /**
     * Function that returns a UserModel
     */
    companion object CREATOR : Parcelable.Creator<UserModel> {
        /**
         * Function that creates from a parcel
         *
         * @param parcel    - Takes a parcel as a parameter
         */
        @RequiresApi(Build.VERSION_CODES.Q)
        override fun createFromParcel(parcel: Parcel): UserModel {
            return UserModel(parcel)
        }

        /**
         * Function that creates a new array
         *
         * @param size  - The size of the array passed as an int
         */
        override fun newArray(size: Int): Array<UserModel?> {
            return arrayOfNulls(size)
        }
    }


}