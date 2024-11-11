import kotlisp.KotlispInstance
import kotlisp.LispConsCell
import kotlisp.LispInt
import kotlisp.LispLeadingElementNotEvaluableAsFunctionException
import kotlisp.LispLeadingSymbolNotFunctionException
import kotlisp.LispLexicalScope
import kotlisp.LispNil
import kotlisp.LispString
import kotlisp.LispSymbol
import kotlisp.LispUndefinedSymbolException
import kotlisp.evaluate
import kotlisp.lispFunction
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

class KotlispInstanceTest {
    @Test
    fun `can evaluate basic arithmetic expression`() {
        // Given
        val expression = TestUtils.ARITHMETIC_EXPRESSION

        // When
        val result = KotlispInstance().evaluate(expression)

        // Then
        assertEquals(LispInt(-8), result)
    }

    @Test
    fun `can pass simple custom bindings and evaluate them`() {
        // Given
        val customScope = LispLexicalScope(
            substitutions = mapOf(
                LispSymbol("hello") to LispString("Hello, world!"),
            )
        )
        val expression = LispSymbol("hello")

        // When
        val result = KotlispInstance(listOf(customScope)).evaluate(expression)

        // Then
        assertEquals(LispString("Hello, world!"), result)
    }


    @Test
    fun `will not evaluate non existing symbol`() {
        // Given
        val expression = LispConsCell(
            LispSymbol("hello"),
            LispNil()
        )

        // When & then
        assertThrows<LispUndefinedSymbolException> {
            KotlispInstance().evaluate(expression)
        }
    }

    @Test
    fun `non-function as first list element fails evaluation`() {
        // Given
        val customScope = LispLexicalScope(
            substitutions = mapOf(
                LispSymbol("hello") to LispString("Hello, world!"),
            )
        )
        val expression = LispConsCell(
            LispSymbol("hello"),
            LispNil()
        )

        // When & then
        assertThrows<LispLeadingSymbolNotFunctionException> {
            KotlispInstance(listOf(customScope)).evaluate(expression)
        }
    }

    @Test
    fun `can pass custom function bindings and evaluate them`() {
        // Given
        val customScope = LispLexicalScope(
            substitutions = mapOf(
                LispSymbol("reverse") to lispFunction<LispString> {
                    arguments {
                        "str" with LispString::class
                    }
                    body {
                        val str = argument<LispString>("str").value
                        LispString(str.reversed())
                    }
                }
            )
        )
        val expression = LispConsCell(
            LispSymbol("reverse"), LispConsCell(
                LispString("hello!"),
                LispNil(),
            ),
        )

        // When
        val result = KotlispInstance(listOf(customScope)).evaluate(expression)

        // Then
        assertEquals(LispString("!olleh"), result)
    }
}