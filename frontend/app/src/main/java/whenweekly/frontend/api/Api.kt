package whenweekly.frontend.api

import com.fasterxml.jackson.databind.ObjectMapper
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

/**
 * API for communication between frontend and backend
 */
object Api {
    /**
     * Client with jackson serialization
     */
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

    data class ApiResponse<T>(val data: T, val status: HttpStatusCode, val message: String)

    /**
     * @return      - Returns exception message
     */
    private fun <T> exceptionResponse(e: Exception): ApiResponse<T?> {
        return ApiResponse(null, HttpStatusCode.UnprocessableEntity, e.message ?: "Unknown error")
    }

    /**
     * API response helper, checks if statuscode is what we want
     */
    private suspend inline fun <reified T> response(response: HttpResponse, expectedStatusCode: HttpStatusCode): ApiResponse<T?> {
        return if (response.status == expectedStatusCode) {
            ApiResponse(response.body(), response.status, "")
        } else {
            ApiResponse(null, response.status, response.bodyAsText())
        }
    }

    /**
     * Serialization of DateTime
     */
    private val objectMapper = ObjectMapper().apply {
        registerModule(JavaTimeModule().apply {
            addSerializer(
                LocalDateTime::class.java,
                LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
            )
        })
    }

    /**
     * Sends a request to the backend
     */
    private suspend fun doRequest(httpMethod: HttpMethod, route: String, body: String? = null): HttpResponse {
        val response = client.request(route) {
            method = httpMethod
            headers {
                if (body != null) {
                    setBody(body)
                }
                append("Content-Type", "application/json")
                if (Globals.Lib.localUUID.isNotEmpty())
                    append("UUID", Globals.Lib.localUUID)
            }
        }
        return response
    }

    /**
     * Get events from the API
     */
    suspend fun getEvents(): ApiResponse<List<EventWithUsers>?> {
        return try {
            val response = doRequest(
                HttpMethod.Get,
                HttpRoutes.EVENTS
            )
            response(response, HttpStatusCode.OK)
        } catch (e: Exception) {
            println(e)
            exceptionResponse(e)
        }
    }

    /**
     * Get user from the API
     */
    suspend fun getUser(): ApiResponse<User?> {
        return try {
            val response = doRequest(
                HttpMethod.Get,
                "${HttpRoutes.USERS}/me"
            )
            response(response, HttpStatusCode.OK)
        } catch (e: Exception) {
            println(e)
            exceptionResponse(e)
        }
    }

    /**
     * Add a user to the database
     */
    suspend fun addUser(name:String): ApiResponse<User?> {
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

            response(response, HttpStatusCode.Created)
        } catch (e: Exception) {
            println(e)
            exceptionResponse(e)
        }
    }

    /**
     * Add an event to the database
     */
    suspend fun addEvent(
        name: String,
        description: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): ApiResponse<EventWithUsers?> {
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

            response(response, HttpStatusCode.Created)
        } catch (e: Exception) {
            println(e)
            exceptionResponse(e)
        }
    }

    /**
     * Join an event by providing inviteCode
     */
    suspend fun joinEvent(inviteCode: String): ApiResponse<EventWithUsers?> {
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
            response(response, HttpStatusCode.Created)
        } catch (e: Exception) {
            println(e)
            exceptionResponse(e)
        }
    }

    /**
     * Kick a user from an event by inputting eventId and userId
     */
    suspend fun kickUserFromEvent(eventId: Int, userId: Int): ApiResponse<Boolean> {
        return try {
            val response = doRequest(
                HttpMethod.Put,
                "${HttpRoutes.EVENTS}/$eventId/kick",
                """
                    {
                        "user_id": $userId
                    }
                    """.trimIndent()
            )
            ApiResponse(response.status == HttpStatusCode.OK, response.status, "")
        } catch (e: Exception) {
            println(e)
            ApiResponse(false, HttpStatusCode.UnprocessableEntity, e.message ?: "Unknown error")
        }
    }

    /**
     * Delete an event from the database
     */
    suspend fun deleteEvent(eventId: Int): Boolean {
        return try {
            val response = doRequest(
                HttpMethod.Delete,
                "${HttpRoutes.EVENTS}/$eventId"
            )
            println(response.bodyAsText())
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            println(e)
            false
        }
    }

    /**
     * Get the available dates for a user in a specific event
     */
    suspend fun getAvailableDates(eventId: Int): ApiResponse<List<LocalDateTime>?> {
        return try {
            val response = doRequest(
                HttpMethod.Get,
                "${HttpRoutes.EVENTS}/$eventId/available-dates"
            )
            response(response, HttpStatusCode.OK)
        } catch (e: Exception) {
            println(e)
            exceptionResponse(e)
        }
    }

    /**
     * Update a user's available dates in a specified eventId
     */
    suspend fun updateAvailableDates(eventId: Int, dates: List<LocalDateTime>): ApiResponse<Boolean> {
        return try {
            val response = doRequest(
                HttpMethod.Patch,
                "${HttpRoutes.EVENTS}/$eventId/available-dates",
                """
                {
                    "available_dates": ${objectMapper.writeValueAsString(dates)}
                }
            """.trimIndent()
            )
            ApiResponse(response.status == HttpStatusCode.OK, response.status, "")
        } catch (e: Exception) {
            println(e)
            ApiResponse(false, HttpStatusCode.UnprocessableEntity, e.message ?: "Unknown error")
        }
    }
}
