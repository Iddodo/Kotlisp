package kotlisp

data class LispFunction<R : LispEvaluable>(
    val arguments: LispArguments,
    private val call: LispArguments.ArgumentContext.() -> R,
): LispEvaluable() {
    override fun toString(): String = super.toString()
    operator fun invoke(inputArguments: List<LispEvaluable>, symbol: LispSymbol? = null, contexts: List<LispLexicalScope> = listOf()): R {
        try {
            val context = arguments.createContext(inputArguments, contexts)
            return context.call()
        } catch (e: LispIllegalParameterException) {
            throw LispIllegalFunctionInvocationException(symbol, arguments, inputArguments, e.message)
        }
    }
}

fun <R : LispEvaluable> lispFunction(
    builderAction: LispFunctionBuilder<R>.() -> Unit
): LispFunction<R> {
    return LispFunctionBuilder<R>().apply(builderAction).build()
}

class LispFunctionBuilder<R : LispEvaluable> {
    private lateinit var functionArguments: LispArguments
    private lateinit var functionBody: LispArguments.ArgumentContext.() -> R

    // Define the arguments block
    fun arguments(block: LispArguments.ArgumentsBuilder.() -> Unit) {
        functionArguments = LispArguments.build(block)
    }

    // Define the function body with access to `argument("name")`
    fun body(body: LispArguments.ArgumentContext.() -> R) {
        functionBody = body
    }

    // Build the LispFunction
    fun build(): LispFunction<R> {
        if (!::functionArguments.isInitialized || !::functionBody.isInitialized) {
            throw IllegalStateException("Arguments and body must be defined")
        }
        return LispFunction(functionArguments, functionBody)
    }
}
