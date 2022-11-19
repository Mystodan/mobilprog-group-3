package whenweekly.routes

import io.ktor.server.request.*
import whenweekly.domain.repository.UserRepository

object Shared {
    fun getUserId(request: ApplicationRequest, userRepository: UserRepository): Int? {
        val uuid = request.headers["UUID"] ?: return null
        userRepository.getUserByUUID(uuid)?.let {
            return it.id!!
        }
        return null
    }
}