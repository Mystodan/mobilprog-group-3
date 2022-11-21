package whenweekly.frontend.api.models
import kotlinx.serialization.Serializable
import java.nio.ByteBuffer
import java.util.UUID

// TODO: merge with UserModel
@Serializable
data class User(
    val id: Int,
    val uuid: ByteArray?,
    val name: String?
) {
    fun uuidToString(): String{
        val bb = ByteBuffer.wrap(uuid)
        return UUID(bb.long, bb.long).toString()
    }
}
