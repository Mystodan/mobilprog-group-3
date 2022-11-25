package whenweekly.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import whenweekly.routes.Constants.BUILD_CONFIG

/**
 * Dev route to protect dev only routes from being accessed in production
 *
 * @param build The build route
 * @return The dev route
 */
fun Route.dev(build: Route.() -> Unit): Route {
    val route = createChild(DevRouteSelector())

    val isDev = environment?.config?.property(BUILD_CONFIG)?.getString().equals("dev", true)
    val plugin = createRouteScopedPlugin("RouteAuthorization") {
        on(AuthenticationChecked) { call ->
            if (!isDev) {
                call.respond(HttpStatusCode.Unauthorized)
            }
        }
    }

    route.install(plugin)
    route.build()
    return route
}

/**
 * Dev route selector
 *
 * @constructor Create empty Dev route selector
 */
class DevRouteSelector : RouteSelector() {
    override fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation {
        return RouteSelectorEvaluation.Transparent
    }

    override fun toString(): String = "auth"
}