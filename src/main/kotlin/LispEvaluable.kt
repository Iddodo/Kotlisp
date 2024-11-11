package kotlisp

sealed class LispEvaluable {
    override fun toString(): String {
        val acc = StringBuilder()
        stringifyLispEvaluable(this, acc)
        return acc.toString()
    }
}

data class LispString(val value: String) : LispEvaluable() {
    override fun toString(): String = super.toString()
}
data class LispInt(val value: Int) : LispEvaluable() {
    override fun toString(): String = super.toString()
}
data class LispSymbol(val value: String) : LispEvaluable() {
    override fun toString(): String = super.toString()
}

class LispNil : LispEvaluable()

fun LispEvaluable.evaluate(contexts: List<LispLexicalScope>): LispEvaluable =
    when (this) {
        is LispString -> this
        is LispInt -> this
        is LispNil -> this
        is LispFunction<out LispEvaluable> -> this
        is LispConsCell -> when {
            quoted -> this
            first is LispFunction<out LispEvaluable> -> {
                first.invoke(rest.asParameterList())
            }
            first is LispSymbol -> {
                val firstValue = contexts.firstNotNullOfOrNull {
                    it.substitutions[first]?.evaluate(contexts)
                } ?: throw LispUndefinedSymbolException(first)

                if (firstValue !is LispFunction<out LispEvaluable>) {
                    throw LispLeadingSymbolNotFunctionException(first)
                }

                firstValue.invoke(rest.asParameterList(), first, contexts)
            }
            else -> throw LispLeadingElementNotEvaluableAsFunctionException(this)
        }
        is LispSymbol -> {
            contexts.firstNotNullOfOrNull {
                it.substitutions[this]?.evaluate(contexts)
            } ?: throw LispUndefinedSymbolException(this)
        }
        else -> throw LispUnsupportedEvaluationException(this)
    }

private fun stringifyLispEvaluable(
    evaluable: LispEvaluable,
    acc: StringBuilder
) {
    when (evaluable) {
        is LispNil -> acc.append("nil")
        is LispString -> acc.append("\"${evaluable.value.replace("\"", "\\\"")}\"")
        is LispInt -> acc.append(evaluable.value)
        is LispSymbol -> acc.append(evaluable.value)
        is LispFunction<out LispEvaluable> -> acc.append("(lambda (${evaluable.arguments.keys.joinToString(" ")}))")
        is LispConsCell -> {
            acc.append("(")
            // Process 'first' separately (this utilizes the stack)
            stringifyLispEvaluable(evaluable.first, acc)
            // Begin tail-recursive processing of 'rest'
            stringifyLispRest(evaluable.rest, acc)
        }
    }
}

private tailrec fun stringifyLispRest(
    rest: LispEvaluable,
    acc: StringBuilder
) {
    when (rest) {
        is LispNil -> acc.append(")")
        is LispConsCell -> {
            acc.append(" ")
            // Process 'first' of 'rest' (this utilizes the stack)
            stringifyLispEvaluable(rest.first, acc)
            // Tail call with 'rest.rest'
            stringifyLispRest(rest.rest, acc)
        }
        else -> {
            // Handle dotted pair notation
            acc.append(" . ")
            stringifyLispEvaluable(rest, acc)
            acc.append(")")
        }
    }
}

internal fun LispEvaluable.asParameterList(): List<LispEvaluable> =
    when (this) {
        is LispNil -> emptyList()
        !is LispConsCell -> listOf(this)
        else -> toList()
    }
