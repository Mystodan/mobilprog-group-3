package whenweekly.models

import kotlinx.serialization.Serializable;

@Serializable
data class Member(val id: String)

val memberStorage = mutableListOf<Member>()