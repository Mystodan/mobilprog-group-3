package whenweekly.routes

import io.ktor.server.request.*
import whenweekly.domain.repository.UserRepository

object Shared {
    /**
     * Get user id via UUID in header
     *
     * @param request The request
     * @param userRepository The user repository
     * @return user id
     */
    fun getUserId(request: ApplicationRequest, userRepository: UserRepository): Int? {
        val uuid = request.headers["UUID"] ?: return null
        userRepository.getUserByUUID(uuid)?.let {
            return it.id
        }
        return null
    }
}