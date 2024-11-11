import kotlisp.LispConsCell
import kotlisp.LispInt
import kotlisp.LispNil
import kotlisp.LispSymbol

object TestUtils {
    val ARITHMETIC_EXPRESSION = LispConsCell(
        LispSymbol("*"), LispConsCell(
            LispInt(2), LispConsCell(
                LispConsCell(
                    LispSymbol("-"), LispConsCell(
                        LispInt(1), LispConsCell(
                            LispConsCell(
                                LispSymbol("+"),
                                LispConsCell(
                                    LispInt(2),
                                    LispConsCell(LispInt(3), LispNil()),
                                ),
                            ),
                            LispNil(),
                        ),
                    ),
                ),
                LispNil(),
            )
        )
    )
}