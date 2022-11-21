package whenweekly.misc

import java.nio.ByteBuffer
import java.util.*

fun UUID.asBytes(): ByteArray{
    val b = ByteBuffer.wrap(ByteArray(16))
    b.putLong(this.mostSignificantBits)
    b.putLong(this.leastSignificantBits)
    return b.array()
}

fun ByteArray.asUUID(): UUID{
    val b = ByteBuffer.wrap(this)
    val mostSignificantBits = b.long
    val leastSignificantBits = b.long
    return UUID(mostSignificantBits, leastSignificantBits)
}