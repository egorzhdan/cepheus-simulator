package build

import build.buildSystem.GCC
import build.buildSystem.Python
import java.io.BufferedReader
import java.io.File
import kotlin.coroutines.experimental.buildSequence

/**
 * @author Egor Zhdan
 */
interface BuildSystem {
    val name: String

    suspend fun compile(sourceFile: File)

    suspend fun run(sourceFile: File, session: Session)

    suspend fun abort()

    suspend fun cleanUp(sourceFile: File)

    companion object {
        val all: MutableList<BuildSystem> = arrayListOf(Python, GCC)
    }
}

fun BuildSystem.register() {
    BuildSystem.all.add(this)
}

fun BufferedReader.sequenceOfLines() = buildSequence<String> {
    use {
        while (true) {
            yield(it.readLine() ?: break)
        }
    }
}
