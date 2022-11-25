package whenweekly.frontend.api

object HttpRoutes {
    private const val BASE_URL = "http://10.0.2.2:8080"
    const val USERS = "$BASE_URL/users"
    const val EVENTS = "$BASE_URL/events"
    const val EVENTS_JOIN = "$EVENTS/join"
}