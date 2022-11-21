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

    private suspend fun getUsers(client: HttpClient): HttpResponse {
        return client.get(USERS_ROUTE){
            contentType(ContentType.Application.Json)
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

        // Invalid UUID
        eventResponse = createEvent(client, event, ByteArray(16))
        assertEquals(HttpStatusCode.Unauthorized, eventResponse.status)

        // Empty name
        event = EventTest(name = "", description = "test description", start_date = "2021-01-01T00:00:00", end_date = "2021-01-01T00:00:00")
        eventResponse = createEvent(client, event, owner.uuid!!)
        assertEquals(HttpStatusCode.BadRequest, eventResponse.status)

        // Empty description
        event = EventTest(name = "test event", description = "", start_date = "2021-01-01T00:00:00", end_date = "2021-01-01T00:00:00")
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
    }
    @Test
    fun testRoutes() = testApplication {
//
//        application {
//            configureRouting()
//        }
//        resetDatabase()
//
//        val client = getClient()
//
//        //createUser(client, "[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16]", "bob")
//        //createUser(client, "[201,123,54,73,139,77,65,46,138,134,93,119,24,17,127,69]", "bob")
//
//        //val user = createUser(client,"test","testUser", HttpStatusCode.Created)
//
//        val users = getUsers(client)
//        assertEquals(1, users.size)
//        val user = users[0]
//
//        createEvent(client, "test event", "Some test event", "{ \"id\": ${user.id} }", LocalDateTime.now(), LocalDateTime.now())
//
//        val events = getEvents(client)
//        assertEquals(1, events.size)
//        val event = events[0]
//        assertEquals("test event", event.name)
//
//        // Join event
//        client.put("$EVENTS_ROUTE/${event.id}/join") {
//            contentType(ContentType.Application.Json)
//            setBody("{ \"id\": ${user.id} }")
//            headers{
//                val testUUID = "test"
//                append("UUID", testUUID)
//            }
//        }
//
//        // Get events for user
//        client.get("$USERS_ROUTE/${user.id}/events").apply {
//            assertEquals(HttpStatusCode.OK, status)
//            println(bodyAsText())
//            val events: List<EventTest> = body()
//            assertEquals(1, events.size)
//        }
    }

    suspend fun createUser(client: HttpClient, uuid: String, name: String, expectedStatusCode: HttpStatusCode): User? {
        val userStr = """
            {
                "uuid": $uuid,
                "name": "$name"
            }
        """.trimIndent()

        val response = client.post(USERS_ROUTE) {
            contentType(ContentType.Application.Json)
            //val userStr = createUserString()
            setBody(userStr)

        }.apply{
            assertEquals(expectedStatusCode, status)
        }
        return response.body()
    }


    suspend fun createEvent(client: HttpClient, name: String, description: String, owner: String, startDate: LocalDateTime, endDate: LocalDateTime) {
        val formatter= DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val eventStr = """
            {
                "name": "$name",
                "description": "$description",
                "owner": $owner,
                "start_date": "${startDate.format(formatter)}",
                "end_date": "${endDate.format(formatter)}"
            }
        """.trimIndent()

        client.post(EVENTS_ROUTE) {
            contentType(ContentType.Application.Json)
            println(eventStr)
            setBody(eventStr)
        }.apply{
            assertEquals(HttpStatusCode.Created, status)
        }
    }

    suspend fun getEvents(client: HttpClient): List<EventTest> {
        client.get(EVENTS_ROUTE).apply {
            assertEquals(HttpStatusCode.OK, status)
            println(bodyAsText())
            val events: List<EventTest> = body()
            return events
        }
    }
    fun resetDatabase() {
        testApplication {
            client.delete("/reset").apply {
                assertEquals(HttpStatusCode.OK, status)
                assertEquals("Database reset", bodyAsText())
            }
        }
    }

    suspend fun setUUID(userId: Int, uuid: JSONArray){

    }
}