package whenweekly.frontend.api

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import io.ktor.serialization.jackson.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.HttpClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import whenweekly.frontend.api.models.EventWithUsers
import whenweekly.frontend.api.models.User
import whenweekly.frontend.app.Globals
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


object Api {
    private val client = HttpClient {
        install(ContentNegotiation) {
            jackson {
                enable(SerializationFeature.INDENT_OUTPUT)
                // java LocalDateTime serialize support
                registerModule(JavaTimeModule().apply {
                    addSerializer(
                        LocalDateTime::class.java,
                        LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    )
                })
            }
        }
    }

    private suspend fun doRequest(httpMethod: HttpMethod, route: String, body: String? = null): HttpResponse {
        val response = client.request(route) {
            method = httpMethod
            headers {
                if (body != null) {
                    setBody(body)
                }
                append("UUID", Globals.Lib.userId)
                append("Content-Type", "application/json")
            }
        }
        return response
    }

    suspend fun getEvents(): List<EventWithUsers> {
        return try {
            val response = doRequest(
                HttpMethod.Get,
                HttpRoutes.EVENTS
            )
            response.body()
        } catch (e: Exception) {
            println(e)
            emptyList()
        }
    }

    suspend fun addUser(name:String): User? {
        return try {
            val response = doRequest(
                HttpMethod.Post,
                HttpRoutes.USERS,
                """
                    {
                        "name": "$name"
                    }
                    """.trimIndent()
            )
            response.body()
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    suspend fun addEvent(
        name: String,
        description: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): EventWithUsers? {
        return try {
            val response = doRequest(
                HttpMethod.Post,
                HttpRoutes.EVENTS,
                """
                    {
                        "name": "$name",
                        "description": "$description",
                        "start_date": "$startDate",
                        "end_date": "$endDate"
                    }
                    """.trimIndent()
            )
            println(response.bodyAsText())
            response.body()
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    suspend fun joinEvent(inviteCode: String): Pair<EventWithUsers?, String?> {
        return try {
            val response = doRequest(
                HttpMethod.Put,
                HttpRoutes.EVENTS_JOIN,
                """
                    {
                        "invite_code": "$inviteCode"
                    }
                    """.trimIndent()
            )
            println(response.bodyAsText())
            if (response.status == HttpStatusCode.OK) {
                Pair(response.body(), null)
            } else {
                Pair(null, response.bodyAsText())
            }
        } catch (e: Exception) {
            println(e)
            Pair(null, e.message)
        }
    }
}
