package whenweekly.frontend.models

import android.os.Parcel
import android.os.Parcelable
import whenweekly.frontend.app.Globals
import java.util.*

/**
 * The data model for a single event using parcelables to "send" information to a receiver that can use that data to put into their layout XML
 */
data class EventModel(
    var eventName: String,
    var startDate: Long,
    var endDate: Long,
    var invCode: String
) : Parcelable {
    var creatorInit:String? = null
    init {
        creatorInit = Globals.Lib.userId
    }

    /**
     * Constructor for the parcels that sets values for the EventModel parcel
     *
     * @param parcel    - Takes a parcel as a parameter
     */
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString().toString(),
    )

    /**
     * Function that writes values to the parcel
     *
     * @param parcel    - Takes a parcel as a parameter
     * @param flags     - Various flags passed as an int
     */
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(eventName)
        parcel.writeLong(startDate)
        parcel.writeLong(endDate)
        parcel.writeString(invCode)
    }

    /**
     * Function for describing contents inside the parcelable
     */
    override fun describeContents(): Int {
        return 0
    }

    /**
     * Function that returns a EventModel
     */
    companion object CREATOR : Parcelable.Creator<EventModel> {
        /**
         * Function that creates from a parcel
         *
         * @param parcel    - Takes a parcel as a parameter
         */
        override fun createFromParcel(parcel: Parcel): EventModel {
            return EventModel(parcel)
        }

        /**
         * Function that creates a new array
         *
         * @param size  - The size of the array passed as an int
         */
        override fun newArray(size: Int): Array<EventModel?> {
            return arrayOfNulls(size)
        }
    }


}