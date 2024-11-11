package kotlisp.core

import kotlisp.LispEvaluable
import kotlisp.LispInt
import kotlisp.LispString
import kotlisp.LispSymbol
import kotlisp.lispFunction


internal val KOTLISP_BASE_MISC_FUNCTION_BINDINGS = mapOf<LispSymbol, LispEvaluable>(
    // TODO: add variadic arguments, implement similarly to Clojure
    LispSymbol("str") to lispFunction<LispString> {
        arguments {
            "expr" with LispEvaluable::class
        }
        body {
            LispString(
                argument<LispEvaluable>("expr").toString()
            )
        }
    },
)