package whenweekly.plugins

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import org.ktorm.jackson.KtormModule
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Configure serialization
 *
 */
fun Application.configureSerialization() {
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
            // ktorm entity serialization support
            registerModule(KtormModule())
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
