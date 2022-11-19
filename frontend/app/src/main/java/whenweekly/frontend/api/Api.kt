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
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.*
import whenweekly.frontend.api.models.Event
import whenweekly.frontend.app.Globals
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal val ApplicationDispatcher: CoroutineDispatcher = Dispatchers.Main

class Api {
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

    @OptIn(DelicateCoroutinesApi::class)
    fun base(callback: (String) -> Unit) {
        GlobalScope.apply {
            launch(ApplicationDispatcher) {
                val result: String = client.get {
                    url(HttpRoutes.BASE_URL)
                }.bodyAsText()
                callback(result)
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getEvents(callback: (List<Event>) -> Unit) {
        GlobalScope.apply {
            launch(ApplicationDispatcher) {
                val response = client.get(HttpRoutes.EVENTS)
                println(response.bodyAsText())
                val events: List<Event> = response.body()
                callback(events)
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun addEvent(name: String, description: String, startDate: LocalDateTime, endDate: LocalDateTime, callback: (Event) -> Unit) {
        GlobalScope.apply {
            launch(ApplicationDispatcher) {
                val response = client.post(HttpRoutes.EVENTS) {
                    // Set UUID header
                    headers{
                        append("UUID", Globals.Lib.userId!!)
                    }
                    setBody(Event(null, name, description, startDate, endDate, null))
                }
                println(response.bodyAsText())
                val event: Event = response.body()
                callback(event)
            }
        }
    }

}
