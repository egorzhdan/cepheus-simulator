/**
 * @author Egor Zhdan
 */
import org.jetbrains.ktor.netty.*
import org.jetbrains.ktor.routing.*
import org.jetbrains.ktor.application.*
import org.jetbrains.ktor.features.*
import org.jetbrains.ktor.host.*
import org.jetbrains.ktor.http.*
import org.jetbrains.ktor.response.*

fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)

    routing {
        get("/") {
            call.respondText("Hello!", ContentType.Text.Html)
        }
    }
}

fun main(args: Array<String>) {
    embeddedServer(Netty,
            port = 5000,
            watchPaths = listOf("BlogAppKt"),
            module = Application::module
    ).start()
}