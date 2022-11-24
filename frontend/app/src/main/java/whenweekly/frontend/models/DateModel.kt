package whenweekly.frontend.models

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import whenweekly.frontend.app.Globals
import java.time.LocalDateTime
import java.util.*

/**
 * The data model for a single availableDate using parcelables to "send" information to a receiver that can use that data to put into their layout XML
 */
data class DateModel(
    var availableDate: LocalDateTime
) /*: Parcelable {
    /**
     * Constructor for the parcels that sets values for the DateModel parcel
     *
     * @param parcel    - Takes a parcel as a parameter
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    constructor(parcel: Parcel) : this(
        parcel.readString().toString()
    )

    /**
     * Function that writes values to the parcel
     *
     * @param parcel    - Takes a parcel as a parameter
     * @param flags     - Various flags passed as an int
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(availableDate)
    }

    /**
     * Function for describing contents inside the parcelable
     */
    override fun describeContents(): Int {
        return 0
    }

    /**
     * Function that returns a DateModel
     */
    companion object CREATOR : Parcelable.Creator<DateModel> {
        /**
         * Function that creates from a parcel
         *
         * @param parcel    - Takes a parcel as a parameter
         */
        @RequiresApi(Build.VERSION_CODES.Q)
        override fun createFromParcel(parcel: Parcel): DateModel {
            return DateModel(parcel)
        }

        /**
         * Function that creates a new array
         *
         * @param size  - The size of the array passed as an int
         */
        override fun newArray(size: Int): Array<DateModel?> {
            return arrayOfNulls(size)
        }
    }


}*/