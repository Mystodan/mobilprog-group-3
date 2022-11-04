package whenweekly.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

fun Application.configureSerialization() {
    // Configure ktormmodule for jackson
    install(ContentNegotiation) {
        json()
    }
}