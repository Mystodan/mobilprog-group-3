package whenweekly

import io.ktor.server.application.*
import whenweekly.database.DatabaseHelper.configureDatabaseConfig
import whenweekly.plugins.configureRequestValidation
import whenweekly.plugins.configureRouting
import whenweekly.plugins.configureSerialization

/**
 * Main
 * The main function is the entry point of the application.
 * @param args
 */
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

/**
 * Module
 * Configure plugins
 */
fun Application.module() {
    configureDatabaseConfig()
    configureRouting()
    configureSerialization()
    configureRequestValidation()
}