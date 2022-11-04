package whenweekly.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.auth(build: Route.() -> Unit): Route {
    val route = createChild(AuthRouteSelector())
    val plugin = createRouteScopedPlugin("RouteAuthorization"){
        on(AuthenticationChecked){ call ->
            if (!isValidUser(call.request)) {
                call.respond(HttpStatusCode.Unauthorized)
            }
        }
    }

    route.install(plugin)
    route.build()
    return route
}

class AuthRouteSelector : RouteSelector() {
    override fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation {
        return RouteSelectorEvaluation.Transparent
    }

    override fun toString(): String = "auth"
}

fun isValidUser(request: ApplicationRequest): Boolean {
    return request.headers["Authorization"] == "Bearer 123"
}