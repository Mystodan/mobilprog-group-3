package whenweekly

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.ktor.server.util.*
import io.ktor.util.*
import org.junit.FixMethodOrder
import org.junit.runners.MethodSorters
import org.ktorm.jackson.KtormModule
import whenweekly.database.entities.User
import whenweekly.misc.asUUID
import whenweekly.plugins.*
import whenweekly.routes.Constants.EVENTS_ROUTE
import whenweekly.routes.Constants.RESET_ROUTE
import whenweekly.routes.Constants.USERS_ROUTE
import whenweekly.routes.EventWithUsers
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.test.*

// Serializable classes for testing purposes
data class UserTest(
    val name: String = ""
)

data class EventTest(
    val name: String = "",
    val description: String = "",
    val start_date: String = "",
    val end_date: String = "",
)


/**
 * Get test client with serialization ktorm support
 *
 * @return test client
 */
fun ApplicationTestBuilder.getClient(): HttpClient {
    return createClient {
        install(ContentNegotiation) {
            jackson {
                enable(SerializationFeature.INDENT_OUTPUT)
                registerModule(KtormModule())
                // Add LocalDateTime serialization support
                registerModule(JavaTimeModule().apply {
                    addSerializer(
                        LocalDateTime::class.java,
                        LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
                    )
                })
            }
        }
    }
}

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ApplicationTest {

    // Object mapper for serialization
    private val objectMapper = ObjectMapper().apply {
        registerModule(KtormModule())
        registerModule(JavaTimeModule().apply {
            addSerializer(
                LocalDateTime::class.java,
                LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
            )
        })
    }

    /**
     * Setup test cases by setting up routing and resetting database
     *
     */
    private fun setupTest() = testApplication {
        application {
            configureRouting()
        }
        resetDatabase()
    }

    private suspend fun createUser(client: HttpClient, user: UserTest): HttpResponse {
        return client.post(USERS_ROUTE) {
            contentType(ContentType.Application.Json)
            setBody(user)
        }
    }

    private suspend fun getUser(client: HttpClient, uuid: ByteArray): HttpResponse {
       return client.get("$USERS_ROUTE/me") {
           contentType(ContentType.Application.Json)
           headers {
               append("UUID", uuid.asUUID().toString())
           }
       }
    }

    private suspend fun createEvent(client: HttpClient, event: EventTest, uuid: ByteArray): HttpResponse {
        return client.post(EVENTS_ROUTE) {
            contentType(ContentType.Application.Json)
            setBody(event)
            headers {
                append("UUID", uuid.asUUID().toString())
            }
        }
    }

    private suspend fun joinEvent(client: HttpClient, inviteCode: String, uuid: ByteArray): HttpResponse {
        return client.put("$EVENTS_ROUTE/join") {
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                    "invite_code": "$inviteCode"
                }
            """.trimIndent()
            )
            headers {
                append("UUID", uuid.asUUID().toString())
            }
        }
    }

    private suspend fun getUsers(client: HttpClient): HttpResponse {
        return client.get(USERS_ROUTE) {
            contentType(ContentType.Application.Json)
        }
    }

    private suspend fun getEvents(client: HttpClient, uuid: ByteArray): HttpResponse {
        return client.get(EVENTS_ROUTE) {
            contentType(ContentType.Application.Json)
            headers {
                append("UUID", uuid.asUUID().toString())
            }
        }
    }

    private suspend fun getEvent(client: HttpClient, eventId: Int): HttpResponse {
        return client.get("$EVENTS_ROUTE/$eventId") {
            contentType(ContentType.Application.Json)
        }
    }

    private suspend fun kickUser(client: HttpClient, eventId: Int, userId: Int, userUUID: ByteArray): HttpResponse {
        return client.put("$EVENTS_ROUTE/$eventId/kick") {
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                    "user_id": $userId
                }
            """.trimIndent()
            )
            headers {
                append("UUID", userUUID.asUUID().toString())
            }
        }
    }

    private suspend fun deleteEvent(client: HttpClient, eventId: Int, userUUID: ByteArray): HttpResponse {
        return client.delete("$EVENTS_ROUTE/$eventId") {
            contentType(ContentType.Application.Json)
            headers {
                append("UUID", userUUID.asUUID().toString())
            }
        }
    }

    private suspend fun updateAvailableDates(
        client: HttpClient,
        eventId: Int,
        dates: List<String>,
        userUUID: ByteArray
    ): HttpResponse {
        val body = """
                {
                    "available_dates": ${objectMapper.writeValueAsString(dates)}
                }
            """.trimIndent()
        return client.patch("$EVENTS_ROUTE/$eventId/available-dates") {
            contentType(ContentType.Application.Json)
            setBody(body)
            headers {
                append("UUID", userUUID.asUUID().toString())
            }
        }
    }

    private suspend fun getAvailableDates(client: HttpClient, eventId: Int, userUUID: ByteArray): HttpResponse {
        return client.get("$EVENTS_ROUTE/$eventId/available-dates") {
            contentType(ContentType.Application.Json)
            headers {
                append("UUID", userUUID.asUUID().toString())
            }
        }
    }

    private suspend fun updateAvailableDates2(
        client: HttpClient,
        eventId: Int,
        dates: List<LocalDateTime>,
        userUUID: ByteArray
    ): HttpResponse {
        val body = """
                {
                    "available_dates": ${objectMapper.writeValueAsString(dates)}
                }
            """.trimIndent()

        return client.patch("$EVENTS_ROUTE/$eventId/available-dates") {
            contentType(ContentType.Application.Json)
            setBody(body)
            headers {
                append("UUID", userUUID.asUUID().toString())
            }
        }
    }
    private suspend fun getAvailableDates2(client: HttpClient, eventId: Int, userUUID: ByteArray): List<LocalDateTime> {
        val response = client.get("$EVENTS_ROUTE/$eventId/available-dates") {
            contentType(ContentType.Application.Json)
            headers {
                append("UUID", userUUID.asUUID().toString())
            }
        }
        return response.body()
    }


    @Test
    fun test0GetUsers() = testApplication {
        setupTest()

        val client = getClient()
        var response = getUsers(client)
        assertEquals(HttpStatusCode.OK, response.status)
        var users = response.body<List<User>>()
        assertEquals(0, users.size)

        // Invalid UUID
        response = getUser(client, ByteArray(16))
        assertEquals(HttpStatusCode.NotFound, response.status)

        val user = UserTest(name = "test")
        response = createUser(client, user)
        assertEquals(HttpStatusCode.Created, response.status)

        response = getUsers(client)
        assertEquals(HttpStatusCode.OK, response.status)
        users = response.body()
        assertEquals(1, users.size)

        response = getUser(client, users[0].uuid!!)
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun test1CreateUser() = testApplication {
        setupTest()

        val client = getClient()
        // Success case
        var user = UserTest(name = "test")
        var response = createUser(client, user)
        assertEquals(HttpStatusCode.Created, response.status)
        val createdUser = response.body<User>()
        val responseUserData = getUser(client, createdUser.uuid!!)
        assertEquals(HttpStatusCode.OK, responseUserData.status)
        val responseUser = responseUserData.body<User>()
        assertEquals(user.name, createdUser.name)
        assertEquals(user.name, responseUser.name)
        assert(createdUser.id > -1)

        // Empty name
        user = UserTest(name = "")
        response = createUser(client, user)
        assertEquals(HttpStatusCode.BadRequest, response.status)

        // Blank name
        user = UserTest(name = " ")
        response = createUser(client, user)
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun test2CreateEvent() = testApplication {
        setupTest()
        val client = getClient()

        val user = UserTest(name = "test")
        val response = createUser(client, user)
        assertEquals(HttpStatusCode.Created, response.status)
        val owner = response.body<User>()

        var event = EventTest(
            name = "test event",
            description = "test description",
            start_date = "2021-01-01T00:00:00",
            end_date = "2021-01-01T00:00:00"
        )

        // Create event
        var eventResponse = createEvent(client, event, owner.uuid!!)
        assertEquals(HttpStatusCode.Created, eventResponse.status)
        val createdEvent = eventResponse.body<EventWithUsers>()
        assertEquals(event.name, createdEvent.event.name)
        assertEquals(LocalDateTime.parse(event.start_date), createdEvent.event.start_date)
        assertEquals(LocalDateTime.parse(event.end_date), createdEvent.event.end_date)
        assertEquals(owner.id, createdEvent.event.owner!!.id)

        // Make sure owner is in the event
        var eventsResponse = getEvents(client, owner.uuid!!)
        assertEquals(HttpStatusCode.OK, eventsResponse.status)
        var events = eventsResponse.body<List<EventWithUsers>>()
        assertEquals(1, events.size)
        assertEquals(createdEvent.event.id, events[0].event.id)
        assertEquals(owner.id, events[0].event.owner!!.id)
        assertEquals(1, events[0].users.size)
        assertEquals(owner.id, events[0].users[0].id)
        assertEquals(null, events[0].users[0].uuid)

        // Empty description
        event = EventTest(
            name = "test event",
            description = "",
            start_date = "2021-01-01T00:00:00",
            end_date = "2021-01-01T00:00:00"
        )
        eventResponse = createEvent(client, event, owner.uuid!!)
        assertEquals(HttpStatusCode.Created, eventResponse.status)

        // Owner should have two events
        eventsResponse = getEvents(client, owner.uuid!!)
        assertEquals(HttpStatusCode.OK, eventsResponse.status)
        events = eventsResponse.body<List<EventWithUsers>>()
        assertEquals(2, events.size)

        /*
         * FAIL cases
         */

        // Invalid UUID
        eventResponse = createEvent(client, event, ByteArray(16))
        assertEquals(HttpStatusCode.Unauthorized, eventResponse.status)

        // Empty name
        event = EventTest(
            name = "",
            description = "test description",
            start_date = "2021-01-01T00:00:00",
            end_date = "2021-01-01T00:00:00"
        )
        eventResponse = createEvent(client, event, owner.uuid!!)
        assertEquals(HttpStatusCode.BadRequest, eventResponse.status)

        // Badly formatted start date
        event = EventTest(
            name = "test event",
            description = "test description",
            start_date = "2021-xx01T00:00:00",
            end_date = "2021-01-01T00:00:00"
        )
        eventResponse = createEvent(client, event, owner.uuid!!)
        assertEquals(HttpStatusCode.BadRequest, eventResponse.status)

        // Badly formatted end date
        event = EventTest(
            name = "test event",
            description = "test description",
            start_date = "2021-01-01T00:00:00",
            end_date = "2021-01-01:00:00"
        )
        eventResponse = createEvent(client, event, owner.uuid!!)
        assertEquals(HttpStatusCode.BadRequest, eventResponse.status)

        // Start date after end date
        event = EventTest(
            name = "test event",
            description = "test description",
            start_date = "2022-01-01T00:00:00",
            end_date = "2021-01-01T00:00:00"
        )
        eventResponse = createEvent(client, event, owner.uuid!!)
        assertEquals(HttpStatusCode.BadRequest, eventResponse.status)
    }

    @Test
    fun test3JoinEvent() = testApplication {
        setupTest()
        val client = getClient()

        val user = UserTest(name = "event owner")
        val response = createUser(client, user)
        assertEquals(HttpStatusCode.Created, response.status)
        val owner = response.body<User>()

        val event = EventTest(
            name = "test event",
            description = "test description",
            start_date = "2021-01-01T00:00:00",
            end_date = "2021-01-01T00:00:00"
        )
        val eventResponse = createEvent(client, event, owner.uuid!!)
        assertEquals(HttpStatusCode.Created, eventResponse.status)
        val eventCreated = eventResponse.body<EventWithUsers>()

        val user2 = UserTest(name = "event joiner")
        val response2 = createUser(client, user2)
        assertEquals(HttpStatusCode.Created, response2.status)
        val joiner = response2.body<User>()

        var joinerEventsResponse = getEvents(client, joiner.uuid!!)
        assertEquals(HttpStatusCode.OK, joinerEventsResponse.status)
        var joinerEvents = joinerEventsResponse.body<List<EventWithUsers>>()
        assertEquals(0, joinerEvents.size)

        // Success case
        var joinResponse = joinEvent(client, eventCreated.event.inviteCode, joiner.uuid!!)
        assertEquals(HttpStatusCode.OK, joinResponse.status)
        val joinEvent = joinResponse.body<EventWithUsers>()
        assertEquals(eventCreated.event.id, joinEvent.event.id)
        assertEquals(2, joinEvent.users.size)
        assertEquals(owner.id, joinEvent.users[0].id)
        assertEquals(joiner.id, joinEvent.users[1].id)

        // Make sure the has joined the event
        joinerEventsResponse = getEvents(client, joiner.uuid!!)
        assertEquals(HttpStatusCode.OK, joinerEventsResponse.status)
        joinerEvents = joinerEventsResponse.body<List<EventWithUsers>>()
        assertEquals(1, joinerEvents.size)
        assertEquals(eventCreated.event.id, joinerEvents[0].event.id)
        assertEquals(owner.id, joinerEvents[0].event.owner!!.id)

        // Join again
        joinResponse = joinEvent(client, eventCreated.event.inviteCode, joiner.uuid!!)
        assertEquals(HttpStatusCode.Conflict, joinResponse.status)

        // Creator tries to join
        joinResponse = joinEvent(client, eventCreated.event.inviteCode, owner.uuid!!)
        assertEquals(HttpStatusCode.Conflict, joinResponse.status)

        // Invalid UUID
        joinResponse = joinEvent(client, eventCreated.event.inviteCode, ByteArray(16))
        assertEquals(HttpStatusCode.Unauthorized, joinResponse.status)

        // Invalid invite code
        joinResponse = joinEvent(client, "123", joiner.uuid!!)
        assertEquals(HttpStatusCode.NotFound, joinResponse.status)
    }

    @Test
    fun test4KickUser() = testApplication {
        setupTest()
        val client = getClient()

        val user = UserTest(name = "event owner")
        val response = createUser(client, user)
        assertEquals(HttpStatusCode.Created, response.status)
        val owner = response.body<User>()

        val event = EventTest(
            name = "test event",
            description = "test description",
            start_date = "2021-01-01T00:00:00",
            end_date = "2021-01-01T00:00:00"
        )
        val eventResponse = createEvent(client, event, owner.uuid!!)
        assertEquals(HttpStatusCode.Created, eventResponse.status)
        val eventCreated = eventResponse.body<EventWithUsers>()

        val user2 = UserTest(name = "event joiner")
        val response2 = createUser(client, user2)
        assertEquals(HttpStatusCode.Created, response2.status)
        val joiner = response2.body<User>()

        var joinerEventsResponse = getEvents(client, joiner.uuid!!)
        assertEquals(HttpStatusCode.OK, joinerEventsResponse.status)
        var joinerEvents = joinerEventsResponse.body<List<EventWithUsers>>()
        assertEquals(0, joinerEvents.size)


        // Success case
        var joinResponse = joinEvent(client, eventCreated.event.inviteCode, joiner.uuid!!)
        assertEquals(HttpStatusCode.OK, joinResponse.status)
        val joinEvent = joinResponse.body<EventWithUsers>()
        assertEquals(eventCreated.event.id, joinEvent.event.id)
        assertEquals(2, joinEvent.users.size)
        assertEquals(owner.id, joinEvent.users[0].id)
        assertEquals(joiner.id, joinEvent.users[1].id)


        val user3 = UserTest(name = "event joiner 2")
        val response3 = createUser(client, user3)
        assertEquals(HttpStatusCode.Created, response3.status)
        val joiner3 = response3.body<User>()

        // user 3 join
        joinResponse = joinEvent(client, eventCreated.event.inviteCode, joiner3.uuid!!)
        assertEquals(HttpStatusCode.OK, joinResponse.status)

        // Make sure the user has joined the event
        joinerEventsResponse = getEvents(client, joiner.uuid!!)
        assertEquals(HttpStatusCode.OK, joinerEventsResponse.status)
        joinerEvents = joinerEventsResponse.body<List<EventWithUsers>>()
        assertEquals(1, joinerEvents.size)
        assertEquals(eventCreated.event.id, joinerEvents[0].event.id)
        assertEquals(owner.id, joinerEvents[0].event.owner!!.id)


        // Try to kick owner as the user
        var kickResponse = kickUser(client, eventCreated.event.id, owner.id, joiner.uuid!!)
        assertEquals(HttpStatusCode.Forbidden, kickResponse.status)

        // Try to kick another user when not owner
        kickResponse = kickUser(client, eventCreated.event.id, joiner3.id, joiner.uuid!!)
        assertEquals(HttpStatusCode.Forbidden, kickResponse.status)

        // Kick user
        kickResponse = kickUser(client, eventCreated.event.id, joiner.id, owner.uuid!!)
        assertEquals(HttpStatusCode.OK, kickResponse.status)

        // Make sure the user has been kicked
        val events = getEvents(client, owner.uuid!!).body<List<EventWithUsers>>()
        assertEquals(2, events[0].users.size)

        // Owner kick owner
        kickResponse = kickUser(client, eventCreated.event.id, owner.id, owner.uuid!!)
        assertEquals(HttpStatusCode.Forbidden, kickResponse.status)

        // User leave event
        kickResponse = kickUser(client, eventCreated.event.id, joiner3.id, joiner3.uuid!!)
        assertEquals(HttpStatusCode.OK, kickResponse.status)

        // Make sure the user has been kicked
        joinerEventsResponse = getEvents(client, joiner.uuid!!)
        assertEquals(HttpStatusCode.OK, joinerEventsResponse.status)
        joinerEvents = joinerEventsResponse.body<List<EventWithUsers>>()
        assertEquals(0, joinerEvents.size)

        // Try to kick again
        kickResponse = kickUser(client, eventCreated.event.id, joiner.id, owner.uuid!!)
        assertEquals(HttpStatusCode.NotFound, kickResponse.status)

        // Try to kick with invalid UUID
        kickResponse = kickUser(client, eventCreated.event.id, joiner.id, ByteArray(16))
        assertEquals(HttpStatusCode.Unauthorized, kickResponse.status)

        // Try to kick with invalid event id
        kickResponse = kickUser(client, 0, joiner.id, owner.uuid!!)
        assertEquals(HttpStatusCode.NotFound, kickResponse.status)

        // Try to kick with invalid user id
        kickResponse = kickUser(client, eventCreated.event.id, 0, owner.uuid!!)
        assertEquals(HttpStatusCode.NotFound, kickResponse.status)

        // Try to kick owner
        kickResponse = kickUser(client, eventCreated.event.id, owner.id, owner.uuid!!)
        assertEquals(HttpStatusCode.Forbidden, kickResponse.status)
    }

    @Test
    fun test5DeleteEvent() = testApplication {
        setupTest()
        val client = getClient()

        val user = UserTest(name = "event owner")
        val response = createUser(client, user)
        assertEquals(HttpStatusCode.Created, response.status)
        val owner = response.body<User>()

        val nonOwner = UserTest(name = "non owner")
        val nonOwnerResponse = createUser(client, nonOwner)
        assertEquals(HttpStatusCode.Created, nonOwnerResponse.status)
        val nonOwnerUser = nonOwnerResponse.body<User>()

        val event = EventTest(
            name = "test event",
            description = "test description",
            start_date = "2021-01-01T00:00:00",
            end_date = "2021-01-01T00:00:00"
        )
        val eventResponse = createEvent(client, event, owner.uuid!!)
        assertEquals(HttpStatusCode.Created, eventResponse.status)
        val eventCreated = eventResponse.body<EventWithUsers>()

        // Try to delete with invalid UUID
        var deleteResponse = deleteEvent(client, eventCreated.event.id, ByteArray(16))
        assertEquals(HttpStatusCode.Unauthorized, deleteResponse.status)

        // Try to delete with invalid event id
        deleteResponse = deleteEvent(client, -1, owner.uuid!!)
        assertEquals(HttpStatusCode.NotFound, deleteResponse.status)

        // Try to delete with non owner
        deleteResponse = deleteEvent(client, eventCreated.event.id, nonOwnerUser.uuid!!)
        assertEquals(HttpStatusCode.Unauthorized, deleteResponse.status)

        // Success case
        deleteResponse = deleteEvent(client, eventCreated.event.id, owner.uuid!!)
        assertEquals(HttpStatusCode.OK, deleteResponse.status)

        // Try to delete again
        deleteResponse = deleteEvent(client, eventCreated.event.id, owner.uuid!!)
        assertEquals(HttpStatusCode.NotFound, deleteResponse.status)
    }

    @Test
    fun test6AvailableDates() = testApplication {
        setupTest()
        val client = getClient()

        val user = UserTest(name = "event owner")
        val response = createUser(client, user)
        assertEquals(HttpStatusCode.Created, response.status)
        val owner = response.body<User>()

        val event = EventTest(
            name = "test event",
            description = "test description",
            start_date = "2021-01-01T00:00:00",
            end_date = "2021-01-05T00:00:00"
        )
        val eventResponse = createEvent(client, event, owner.uuid!!)
        assertEquals(HttpStatusCode.Created, eventResponse.status)
        val eventCreated = eventResponse.body<EventWithUsers>()

        // Make sure we can get the available dates after event creation
        val availableDatesResponse = getAvailableDates(client, eventCreated.event.id, owner.uuid!!)
        assertEquals(HttpStatusCode.OK, availableDatesResponse.status)

        val dates = listOf(
            "2021-01-02T00:00:00",
            "2021-01-03T00:00:00",
            "2021-01-04T00:00:00"
        )

        // Try to update with invalid UUID
        var updateResponse = updateAvailableDates(client, eventCreated.event.id, dates, ByteArray(16))
        assertEquals(HttpStatusCode.Unauthorized, updateResponse.status)

        // Try to update with invalid event id
        updateResponse = updateAvailableDates(client, -1, dates, owner.uuid!!)
        assertEquals(HttpStatusCode.NotFound, updateResponse.status)

        // Try to update with invalid date
        updateResponse =
            updateAvailableDates(client, eventCreated.event.id, listOf("2021-x1-01T00:00:00"), owner.uuid!!)
        assertEquals(HttpStatusCode.BadRequest, updateResponse.status)

        // Success case
        updateResponse = updateAvailableDates(client, eventCreated.event.id, dates, owner.uuid!!)
        assertEquals(HttpStatusCode.OK, updateResponse.status)

        // Make sure the dates have been updated
        val availableDatesResp = getAvailableDates(client, eventCreated.event.id, owner.uuid!!)
        val availableDates = availableDatesResp.body<List<String>>()
        assertEquals(3, availableDates.size)
        // NOTE: Not sure if these are guaranteed to be in order.
        assertEquals("2021-01-02T00:00:00", availableDates[0])
        assertEquals("2021-01-03T00:00:00", availableDates[1])
        assertEquals("2021-01-04T00:00:00", availableDates[2])

        // Try to update with date before event start date
        updateResponse =
            updateAvailableDates(client, eventCreated.event.id, listOf("2020-01-01T00:00:00"), owner.uuid!!)
        assertEquals(HttpStatusCode.BadRequest, updateResponse.status)

        // Try to update with date after event end date
        updateResponse =
            updateAvailableDates(client, eventCreated.event.id, listOf("2021-01-06T00:00:00"), owner.uuid!!)
        assertEquals(HttpStatusCode.BadRequest, updateResponse.status)

        val dates2 = getAvailableDates2(client, eventCreated.event.id, owner.uuid!!)
        println(dates2)

        updateAvailableDates2(client, eventCreated.event.id, dates2, owner.uuid!!)
    }

    /**
     * Reset the database
     *
     */
    private fun resetDatabase() {
        testApplication {
            client.delete(RESET_ROUTE).apply {
                assertEquals(HttpStatusCode.OK, status)
                assertEquals("Database reset", bodyAsText())
            }
        }
    }
}