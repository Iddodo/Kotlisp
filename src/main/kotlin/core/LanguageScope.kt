package kotlisp.core

import kotlisp.LispLexicalScope

internal val KOTLISP_LANGUAGE_SCOPE = LispLexicalScope(
    substitutions = listOf(
        KOTLISP_BASE_ARITHMETIC_FUNCTION_BINDINGS,
        KOTLISP_BASE_MISC_FUNCTION_BINDINGS,
    ).fold(emptyMap()) { acc, bindings ->
        acc + bindings
    }
)
