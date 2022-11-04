package whenweekly.frontend.api

import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.*

internal val ApplicationDispatcher: CoroutineDispatcher = Dispatchers.Main

class Api {
    private val client = HttpClient()

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
}
