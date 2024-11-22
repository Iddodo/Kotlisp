import TestUtils.ARITHMETIC_EXPRESSION
import kotlisp.LispConsCell
import kotlisp.LispInt
import kotlisp.LispNil
import kotlisp.LispParser
import kotlisp.LispString
import kotlisp.LispSymbol
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class LispParserTest {
    @Test
    fun `can parse string`() {
        val result = LispParser("\"hello, world!\"").reify()[0]
        assertIs<LispString>(result)
        assertEquals("hello, world!", result.value)
    }

    @Test
    fun `can parse integer`() {
        val result = LispParser("12345").reify()[0]
        assertIs<LispInt>(result)
        assertEquals(12345, result.value)
    }

    @Test
    fun `can parse empty list`() {
        val result = LispParser("()").reify()[0]
        assertIs<LispNil>(result)
    }

    @Test
    fun `can parse simple list`() {
        val result = LispParser("(* 2 (- 1 (+ 2 3)))").reify()[0]
        assertIs<LispConsCell>(result)
        assertEquals(ARITHMETIC_EXPRESSION, result)
        assertFalse(result.quoted)
    }

    @Test
    fun `can parse dot notation`() {
        val result = LispParser("(1 . \"string\")").reify()[0]
        assertIs<LispConsCell>(result)
        assertEquals(LispInt(1), result.first)
        assertEquals(LispString("string"), result.rest)
        assertFalse(result.quoted)
    }

    @Test
    fun `can parse quoted list`() {
        val result = LispParser("'(hello \"world\")").reify()[0]
        assertIs<LispConsCell>(result)
        assertTrue(result.quoted)
    }

    @Test
    fun `can parse quoted symbol`() {
        val result = LispParser("'hello").reify()[0]
        assertIs<LispSymbol>(result)
        assert(result.quoted)
    }
}