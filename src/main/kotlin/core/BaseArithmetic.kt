package kotlisp.core

import kotlisp.LispEvaluable
import kotlisp.LispInt
import kotlisp.LispString
import kotlisp.LispSymbol
import kotlisp.lispFunction

internal val KOTLISP_BASE_ARITHMETIC_FUNCTION_BINDINGS = mapOf<LispSymbol, LispEvaluable>(
    LispSymbol("*") to lispFunction<LispInt> {
        arguments {
            "a" with LispInt::class
            "b" with LispInt::class
        }
        body {
            LispInt(
                argument<LispInt>("a").value * argument<LispInt>("b").value,
            )
        }
    },
    LispSymbol("-") to lispFunction<LispInt> {
        arguments {
            "a" with LispInt::class
            "b" with LispInt::class
        }
        body {
            LispInt(
                argument<LispInt>("a").value - argument<LispInt>("b").value,
            )
        }
    },

    LispSymbol("+") to lispFunction<LispInt> {
        arguments {
            "a" with LispInt::class
            "b" with LispInt::class
        }
        body {
            LispInt(
                argument<LispInt>("a").value + argument<LispInt>("b").value,
            )
        }
    },
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