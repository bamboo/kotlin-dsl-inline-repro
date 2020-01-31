package repro

import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.charset.Charset
import kotlin.test.Test
import kotlin.test.assertEquals

class ReproTest {

    @Test
    fun `provider type is inferred from Callable generic interface argument`() {

        assertEquals(
            "class java.lang.String",
            standardOutputOf {
                printInferredProviderType()
            }.trim()
        )
    }

    private
    fun standardOutputOf(block: () -> Unit): String {
        val output = ByteArrayOutputStream()
        val previous = System.out
        System.setOut(PrintStream(output))
        try {
            block()
        } finally {
            System.setOut(previous)
        }
        return output.toString(Charset.defaultCharset())
    }
}
