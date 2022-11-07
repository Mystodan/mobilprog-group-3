package whenweekly.frontend.api.models
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val uuid: ByteArray,
    val name: String
)
