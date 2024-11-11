package kotlisp

sealed class LispException(message: String) : Exception(message)
class LispUndefinedSymbolException(symbol: LispSymbol) : LispException(
    "Undefined symbol $symbol"
)
class LispLeadingSymbolNotFunctionException(symbol: LispSymbol) : LispException(
    "Leading symbol $symbol is not a function"
)
class LispLeadingElementNotEvaluableAsFunctionException(evaluable: LispEvaluable) : LispException(
    "Leading element $evaluable cannot be evaluated as function"
)
class LispUnsupportedEvaluationException(evaluable: LispEvaluable) : LispException(
    "Unsupported evaluation of $evaluable"
)

class LispIllegalParameterException(message: String) : LispException(message)

class LispIllegalFunctionInvocationException(
    symbol: LispSymbol?,
    expectedArguments: LispArguments,
    inputArguments: List<LispEvaluable>,
    message: String?,
) : LispException(buildString {
    append("Failed to invoke function ")
    append(symbol?.let { "with symbol: '$it'" } ?: "with unknown symbol")
    append(" due to illegal parameters.\n")
    append("Expected: $expectedArguments\n")
    append("Got: $inputArguments\n")
    append("Message: $message")
})
