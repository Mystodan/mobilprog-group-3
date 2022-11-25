package whenweekly.frontend.models

import android.os.Parcel
import android.os.Parcelable
import kotlinx.serialization.Serializable
import whenweekly.frontend.app.Globals

/**
 * The data model for a single event using parcelables to "send" information to a receiver that can use that data to put into their layout XML
 */
@Serializable
data class EventModel(
    var eventName: String,
    var startDate: Long,
    var endDate: Long,
    var invCode: String,
    var eventId:Int,
    var ownerId:Int? = Globals.Lib.CurrentUser?.id
) : Parcelable {

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
        parcel.readInt(),
        parcel.readInt()
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
        parcel.writeInt(eventId)
        parcel.writeInt(ownerId!!)
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