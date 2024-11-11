package kotlisp

// TODO: scope possibility of merging with existing ArgumentContext
class LispLexicalScope(
    val substitutions: Map<LispSymbol, LispEvaluable> = emptyMap()
)
