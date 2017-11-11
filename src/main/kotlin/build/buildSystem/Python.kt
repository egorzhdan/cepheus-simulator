package build.buildSystem

import build.BuildSystem
import build.Session
import build.sequenceOfLines
import java.io.File

/**
 * @author Egor Zhdan
 */
object Python : BuildSystem {
    override val name = "Python 3.6"
    private var process: Process? = null

    override suspend fun compile(sourceFile: File) {
        check(sourceFile.canRead())
    }

    override suspend fun run(sourceFile: File, session: Session) {
        process = Runtime.getRuntime().exec("python3 " + sourceFile.absolutePath + "")
        val reader = process!!.inputStream.bufferedReader()
        val writer = process!!.outputStream.bufferedWriter()

        reader.sequenceOfLines().forEach {
            println("printed $it")
            val response = session.handleOutput(it)

            println("response = $response")
            if (process == null || !process!!.isAlive) {
                return
            }
            if (response != null) {
                writer.write(response)
                writer.newLine()
                writer.flush()
            }
        }

        process!!.errorStream.bufferedReader().readLines().forEach {
            session.handleError(it)
        }
    }

    override suspend fun abort() {
        process?.destroyForcibly()
    }

    override suspend fun cleanUp(sourceFile: File) {
        sourceFile.delete()
    }
}
