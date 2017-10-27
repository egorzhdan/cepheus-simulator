/**
 * @author Egor Zhdan
 */
import build.Reporter
import build.Session
import build.python.Python
import maze.Field
import maze.Move
import maze.Position
import org.jetbrains.ktor.application.Application
import org.jetbrains.ktor.application.install
import org.jetbrains.ktor.content.files
import org.jetbrains.ktor.content.static
import org.jetbrains.ktor.features.AutoHeadResponse
import org.jetbrains.ktor.features.CallLogging
import org.jetbrains.ktor.features.DefaultHeaders
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.routing
import org.jetbrains.ktor.websocket.*
import java.io.File
import java.io.PrintWriter
import java.time.Duration
import java.util.*

fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(AutoHeadResponse)
    install(WebSockets) {
        pingPeriod = Duration.ofMinutes(1)
    }

    routing {
        static("static") {
            files("static")
        }

        get("/") {
            call.respondText(Frontend.makeIndexHTML(), ContentType.Text.Html)
        }

        get("/simulator") {
            call.respondText(Frontend.makeSimulatorHTML(), ContentType.Text.Html)
        }
        webSocket("/simulator/ws") {
            send(Frame.Text("Server ready"))

            val maybeFieldData = incoming.receive()
            if (maybeFieldData !is Frame.Text) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Invalid field received"))
                return@webSocket
            }
            val fieldData = maybeFieldData.readText()

            val field = Field.parse(fieldData)
            send(Frame.Text("Field compiled"))

            val maybeCode = incoming.receive()
            if (maybeCode !is Frame.Text) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Invalid code received"))
                return@webSocket
            }
            val code = maybeCode.readText()

            val buildSystem = Python
            val codeID = Random().nextInt()
            val file = File(System.getProperty("user.dir") + "/tmp/simulator_code$codeID.txt")

            val writer = PrintWriter(file)
            writer.write(code)
            writer.close()

            buildSystem.compile(file)

            send(Frame.Text("Code compiled"))

            var finished = false
            val session = Session(field, object : Reporter {
                override suspend fun moved(move: Move, currentPosition: Position) {
                    send(Frame.Text("/robot move $move to {${currentPosition.row}, ${currentPosition.col}}"))
                }

                override suspend fun badMoveAttempted(move: Move, fromPosition: Position) {
                    buildSystem.abort()
                    send(Frame.Text("/fail to move $move from {${fromPosition.row}, ${fromPosition.col}}"))
                    finished()
                }

                override suspend fun debugOutputReceived(output: String) {
                    send(Frame.Text("/debug $output"))
                }

                override suspend fun errorPrinted(output: String) {
                    send(Frame.Text("/error $output"))
                }

                override suspend fun finished() {
                    send(Frame.Text("/finish"))

                    finished = true
                    file.delete()
                    close(CloseReason(CloseReason.Codes.NORMAL, "Finished"))
                }
            })
            send(Frame.Text("Running code..."))
            buildSystem.run(file, session)

//            incoming.consumeEach { frame ->
//                if (frame is Frame.Text) {
//                    println(frame.readText())
//                }
//            }

            if (!finished) session.finish()
        }
    }
}

fun main(args: Array<String>) {
    val envPort = System.getenv("PORT")
    embeddedServer(Netty,
            port = if (envPort != null) envPort.toInt() else 5000,
            watchPaths = listOf("ApplicationKt"),
            module = Application::module
    ).start()
}