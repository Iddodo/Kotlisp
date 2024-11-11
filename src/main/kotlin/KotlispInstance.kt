package kotlisp

import kotlisp.core.KOTLISP_LANGUAGE_SCOPE

// TODO: add state, currently only serves as front
class KotlispInstance(private val baseScopes: List<LispLexicalScope> = emptyList()) {
    fun evaluate(evaluable: LispEvaluable): LispEvaluable =
        evaluable.evaluate(baseScopes + listOf(KOTLISP_LANGUAGE_SCOPE))
}