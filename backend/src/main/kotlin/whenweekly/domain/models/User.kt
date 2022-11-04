package whenweekly.domain.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class User(
    val id: Int = -1,
    val uuid: ByteArray,
    val name: String
)
