package kotlisp

import kotlin.reflect.KClass

class LispArguments private constructor(private val expectedArguments: Map<String, KClass<out LispEvaluable>>) {
    val keys
        get() = expectedArguments.keys

    // Validate arguments against expected types
    private fun evaluateAndValidateParameters(args: List<LispEvaluable>, contexts: List<LispLexicalScope>): List<LispEvaluable> {
        if (args.size != expectedArguments.size) {
            throw LispIllegalParameterException("Expected ${expectedArguments.size} arguments, but got ${args.size}")
        }

        return expectedArguments.entries.mapIndexed { index, (name, expectedType) ->
            val arg = args[index]
            val evaluatedArg = arg.evaluate(contexts)
            if (!expectedType.isInstance(evaluatedArg)) {
                throw LispIllegalParameterException(
                    "Argument '$name' at position $index expected type ${expectedType.simpleName}, " +
                            "but got ${arg::class.simpleName}"
                )
            }
            evaluatedArg
        }
    }

    // Create a context for accessing arguments by name
    fun createContext(args: List<LispEvaluable>, contexts: List<LispLexicalScope>): ArgumentContext {
        val evaluatedArguments = evaluateAndValidateParameters(args, contexts)
        return ArgumentContext(evaluatedArguments)
    }

    // Inner class to provide access to arguments by name
    inner class ArgumentContext(private val args: List<LispEvaluable>) {
        // Access an argument by its name
        fun <T : LispEvaluable> argument(name: String): T {
            val index = expectedArguments.keys.indexOf(name)
            if (index == -1) {
                throw IllegalArgumentException("Argument '$name' not found in expected arguments.")
            }
            val arg = args[index]
            @Suppress("UNCHECKED_CAST")
            return arg as T
        }
    }

    companion object {
        // Builder function for LispArguments
        fun build(block: ArgumentsBuilder.() -> Unit): LispArguments {
            val builder = ArgumentsBuilder()
            builder.apply(block)
            return LispArguments(builder.argumentPairs)
        }
    }

    // Builder for setting up argument name/type pairs
    class ArgumentsBuilder {
        val argumentPairs = mutableMapOf<String, KClass<out LispEvaluable>>()

        // Define an argument by specifying its name and type
        infix fun String.with(type: KClass<out LispEvaluable>) {
            argumentPairs.put(this, type)
        }
    }


    override fun toString(): String {
        return expectedArguments.toString()
    }

    override fun hashCode(): Int {
        return expectedArguments.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is LispArguments) {
            return false
        }
        return expectedArguments == other.expectedArguments
    }
}
