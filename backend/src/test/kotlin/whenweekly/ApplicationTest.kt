package whenweekly

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.util.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlin.test.*
import io.ktor.server.testing.*
import whenweekly.plugins.*
import whenweekly.routes.Constants.EVENTS_ROUTE
import whenweekly.routes.Constants.USERS_ROUTE
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.mysql.cj.xdevapi.JsonString
import io.ktor.client.plugins.*
import io.ktor.serialization.jackson.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.server.application.*
import io.ktor.server.util.*
import org.json.simple.JSONArray
import org.junit.FixMethodOrder
import org.junit.runners.MethodSorters
import org.ktorm.jackson.KtormModule
import whenweekly.database.entities.Event
import whenweekly.database.entities.User
import whenweekly.database.schemas.UserTable
import whenweekly.misc.asUUID
import whenweekly.routes.Constants.RESET_ROUTE
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

data class UserTest(
    val name: String = ""
)
data class EventTest(
    val name: String = "",
    val description: String = "",
    val start_date: String = "",
    val end_date: String = "",
)

fun ApplicationTestBuilder.getClient(): HttpClient {
    return createClient{
            install(ContentNegotiation) {
                jackson {
                    enable(SerializationFeature.INDENT_OUTPUT)
                    // java LocalDateTime serialize support

                    registerModule(KtormModule())
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

    private suspend fun createEvent(client: HttpClient, event: EventTest, uuid: ByteArray): HttpResponse {
        return client.post(EVENTS_ROUTE) {
            contentType(ContentType.Application.Json)
            setBody(event)
            headers{
                append("UUID", uuid.asUUID().toString())
            }
        }
    }

    private suspend fun userJoinEvent(client: HttpClient, eventId: Int, uuid: ByteArray): HttpResponse {
        return client.put("$EVENTS_ROUTE/$eventId/join") {
            contentType(ContentType.Application.Json)
            headers{
                append("UUID", uuid.asUUID().toString())
            }
        }
    }

    private suspend fun getUsers(client: HttpClient): HttpResponse {
        return client.get(USERS_ROUTE){
            contentType(ContentType.Application.Json)
        }
    }

    private suspend fun getEvents(client: HttpClient, uuid: ByteArray): HttpResponse {
        return client.get(EVENTS_ROUTE){
            contentType(ContentType.Application.Json)
            headers{
                append("UUID", uuid.asUUID().toString())
            }
        }
    }

    @Test
    fun test0GetUsers() = testApplication {
        setupTest()

        val client = getClient()
        var response = getUsers(client)
        assertEquals(HttpStatusCode.OK, response.status)
        var users = response.body<List<User>>()
        assertEquals(0, users.size)

        val user = UserTest(name = "test")
        response = createUser(client, user)
        assertEquals(HttpStatusCode.Created, response.status)

        response = getUsers(client)
        assertEquals(HttpStatusCode.OK, response.status)
        users = response.body()
        assertEquals(1, users.size)
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
        val responseUser = client.get("$USERS_ROUTE/${createdUser.id}").body<User>()
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

        var event = EventTest(name = "test event", description = "test description", start_date = "2021-01-01T00:00:00", end_date = "2021-01-01T00:00:00")
        var eventResponse = createEvent(client, event, owner.uuid!!)
        assertEquals(HttpStatusCode.Created, eventResponse.status)
        val createdEvent = eventResponse.body<Event>()
        assertEquals(event.name, createdEvent.name)
        assertEquals(LocalDateTime.parse(event.start_date), createdEvent.start_date)
        assertEquals(LocalDateTime.parse(event.end_date), createdEvent.end_date)
        assertEquals(owner.id, createdEvent.owner!!.id)

        // Make sure owner is in the event
        var eventsResponse = getEvents(client, owner.uuid!!)
        assertEquals(HttpStatusCode.OK, eventsResponse.status)
        var events = eventsResponse.body<List<Event>>()
        assertEquals(1, events.size)
        assertEquals(createdEvent.id, events[0].id)
        assertEquals(owner.id, events[0].owner!!.id)

        // Empty description
        event = EventTest(name = "test event", description = "", start_date = "2021-01-01T00:00:00", end_date = "2021-01-01T00:00:00")
        eventResponse = createEvent(client, event, owner.uuid!!)
        assertEquals(HttpStatusCode.Created, eventResponse.status)

        // Owner should have two events
        eventsResponse = getEvents(client, owner.uuid!!)
        assertEquals(HttpStatusCode.OK, eventsResponse.status)
        events = eventsResponse.body<List<Event>>()
        assertEquals(2, events.size)

        /*
         * FAIL cases
         */

        // Invalid UUID
        eventResponse = createEvent(client, event, ByteArray(16))
        assertEquals(HttpStatusCode.Unauthorized, eventResponse.status)

        // Empty name
        event = EventTest(name = "", description = "test description", start_date = "2021-01-01T00:00:00", end_date = "2021-01-01T00:00:00")
        eventResponse = createEvent(client, event, owner.uuid!!)
        assertEquals(HttpStatusCode.BadRequest, eventResponse.status)

        // Badly formatted start date
        event = EventTest(name = "test event", description = "test description", start_date = "2021-xx01T00:00:00", end_date = "2021-01-01T00:00:00")
        eventResponse = createEvent(client, event, owner.uuid!!)
        assertEquals(HttpStatusCode.BadRequest, eventResponse.status)

        // Badly formatted end date
        event = EventTest(name = "test event", description = "test description", start_date = "2021-01-01T00:00:00", end_date = "2021-01-01:00:00")
        eventResponse = createEvent(client, event, owner.uuid!!)
        assertEquals(HttpStatusCode.BadRequest, eventResponse.status)

        // Start date after end date
        event = EventTest(name = "test event", description = "test description", start_date = "2022-01-01T00:00:00", end_date = "2021-01-01T00:00:00")
        eventResponse = createEvent(client, event, owner.uuid!!)
        assertEquals(HttpStatusCode.BadRequest, eventResponse.status)
    }
    @Test
    fun test3JoinEvent() = testApplication{
        setupTest()
        val client = getClient()

        val user = UserTest(name = "event owner")
        val response = createUser(client, user)
        assertEquals(HttpStatusCode.Created, response.status)
        val owner = response.body<User>()

        val event = EventTest(name = "test event", description = "test description", start_date = "2021-01-01T00:00:00", end_date = "2021-01-01T00:00:00")
        val eventResponse = createEvent(client, event, owner.uuid!!)
        assertEquals(HttpStatusCode.Created, eventResponse.status)
        val eventCreated = eventResponse.body<Event>()

        val user2 = UserTest(name = "event joiner")
        val response2 = createUser(client, user2)
        assertEquals(HttpStatusCode.Created, response2.status)
        val joiner = response2.body<User>()

        var joinerEventsResponse = getEvents(client, joiner.uuid!!)
        assertEquals(HttpStatusCode.OK, joinerEventsResponse.status)
        var joinerEvents = joinerEventsResponse.body<List<Event>>()
        assertEquals(0, joinerEvents.size)

        // Success case
        var joinResponse = userJoinEvent(client, eventCreated.id, joiner.uuid!!)
        assertEquals(HttpStatusCode.OK, joinResponse.status)

        // Make sure the has joined the event
        joinerEventsResponse = getEvents(client, joiner.uuid!!)
        assertEquals(HttpStatusCode.OK, joinerEventsResponse.status)
        joinerEvents = joinerEventsResponse.body<List<Event>>()
        assertEquals(1, joinerEvents.size)
        assertEquals(eventCreated.id, joinerEvents[0].id)
        assertEquals(owner.id, joinerEvents[0].owner!!.id)

        // Join again
        joinResponse = userJoinEvent(client, eventCreated.id, joiner.uuid!!)
        assertEquals(HttpStatusCode.Conflict, joinResponse.status)

        // Creator tries to join
        joinResponse = userJoinEvent(client, eventCreated.id, owner.uuid!!)
        assertEquals(HttpStatusCode.Conflict, joinResponse.status)

        // Invalid UUID
        joinResponse = userJoinEvent(client, eventCreated.id, ByteArray(16))
        assertEquals(HttpStatusCode.Unauthorized, joinResponse.status)

        // Invalid event id
        joinResponse = userJoinEvent(client, -1, joiner.uuid!!)
        assertEquals(HttpStatusCode.NotFound, joinResponse.status)
    }

    private fun resetDatabase() {
        testApplication {
            client.delete(RESET_ROUTE).apply {
                assertEquals(HttpStatusCode.OK, status)
                assertEquals("Database reset", bodyAsText())
            }
        }
    }
}