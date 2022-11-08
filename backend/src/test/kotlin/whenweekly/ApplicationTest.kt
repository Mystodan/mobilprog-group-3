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
import whenweekly.database.entities.User
import whenweekly.plugins.*
import whenweekly.routes.Constants.EVENTS_ROUTE
import whenweekly.routes.Constants.USERS_ROUTE
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import io.ktor.client.plugins.*
import io.ktor.serialization.jackson.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.server.application.*
import io.ktor.server.util.*
import kotlinx.serialization.Serializable
import org.junit.FixMethodOrder
import org.junit.runners.MethodSorters
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class UserTest(
    val id: Int,
    val uuid: ByteArray = ByteArray(0),
    val name: String = ""
)

data class EventTest(
    val id: Int,
    val name: String = "",
    val description: String = "",
    val start_date: LocalDateTime = LocalDateTime.now(),
    val end_date: LocalDateTime = LocalDateTime.now(),
    val owner: UserTest = UserTest(-1),
)

fun ApplicationTestBuilder.getClient(): HttpClient {
    return createClient{
            install(ContentNegotiation) {
                jackson {
                    enable(SerializationFeature.INDENT_OUTPUT)
                    // java LocalDateTime serialize support
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
    @Test
    fun testRoutes() = testApplication {
        application {
            configureRouting()
        }
        resetDatabase()

        val client = getClient()

        createUser(client, "[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16]", "bob")

        val users = getUsers(client)
        assertEquals(1, users.size)
        val user = users[0]

        createEvent(client, "test event", "Some test event", "{ \"id\": ${user.id} }", LocalDateTime.now(), LocalDateTime.now())

        val events = getEvents(client)
        assertEquals(1, events.size)
        val event = events[0]
        assertEquals("test event", event.name)

        // Join event
        client.put("$EVENTS_ROUTE/${event.id}/join") {
            contentType(ContentType.Application.Json)
            setBody("{ \"id\": ${user.id} }")
        }

        // Get events for user
        client.get("$USERS_ROUTE/${user.id}/events").apply {
            assertEquals(HttpStatusCode.OK, status)
            println(bodyAsText())
            val events: List<EventTest> = body()
            assertEquals(1, events.size)
        }
    }

    suspend fun createUser(client: HttpClient, uuid: String, name: String) {
        val userStr = """
            {
                "uuid": $uuid,
                "name": "$name"
            }
        """.trimIndent()

        client.post(USERS_ROUTE) {
            contentType(ContentType.Application.Json)
            //val userStr = createUserString()
            setBody(userStr)

        }.apply{
            assertEquals(HttpStatusCode.Created, status)
        }
    }

    suspend fun getUsers(client: HttpClient): List<UserTest> {
        client.get(USERS_ROUTE).apply {
            assertEquals(HttpStatusCode.OK, status)
            val users: List<UserTest> = body()
            return users
        }

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
}