package build.buildSystem

import build.BuildSystem
import build.Session
import build.sequenceOfLines
import maze.CompilationErrorException
import java.io.File

/**
 * @author Egor Zhdan
 */
object GCC : BuildSystem {
    override val name = "C11 GCC 5"
    private var process: Process? = null

    private fun cFile(sourceFile: File) = File(sourceFile.parent + "/" + sourceFile.nameWithoutExtension + ".c")
    private fun runFile(sourceFile: File) = File(sourceFile.parent + "/" + sourceFile.nameWithoutExtension)
    private fun headerFile() = File(System.getProperty("user.dir") + "/static/simulator-header.h")

    override suspend fun compile(sourceFile: File) {
        check(sourceFile.canRead())

        val gccName = System.getenv("GCC_NAME") ?: "gcc-5"

        val cFile = cFile(sourceFile)
        sourceFile.copyTo(cFile)
        val compileProcess = Runtime.getRuntime().exec("$gccName -o ${runFile(sourceFile).absolutePath} ${headerFile().absolutePath} ${cFile.absolutePath} -std=c11 -I${headerFile().parent} -w")

        val errors = compileProcess.errorStream.bufferedReader().readLines()
        if (errors.isNotEmpty()) {
            throw CompilationErrorException(errors.joinToString("\n"))
        }
    }

    override suspend fun run(sourceFile: File, session: Session) {
        val runFile = runFile(sourceFile)
        process = Runtime.getRuntime().exec(runFile.absolutePath)
        val reader = process!!.inputStream.bufferedReader()
        val writer = process!!.outputStream.bufferedWriter()

        reader.sequenceOfLines({
            writer.close()
        }).forEach {
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
        cFile(sourceFile).delete()
        runFile(sourceFile).delete()
    }
}