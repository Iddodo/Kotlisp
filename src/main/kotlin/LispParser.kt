package kotlisp

class LispParser(private val src: String) {
    private val iterator = object : CharIterator() {
        private var index = 0
        override fun nextChar(): Char = src[index++]
        override fun hasNext(): Boolean = index < src.length
        fun reset() {
            index = 0
        }
        fun tryNext() = if (hasNext()) next() else null
        fun tryPeek() = if (hasNext()) src[index] else null
    }

    fun reify(): List<LispEvaluable> {
        iterator.reset()
        return recur()
    }
    private tailrec fun recur(acc: List<LispEvaluable> = listOf()): List<LispEvaluable> = when (val char = iterator.tryNext()) {
        null -> acc
        else -> {
            val nextEvaluable = reifyEvaluableByChar(char)
                ?: throw LispParserGeneralException()
            recur(acc + nextEvaluable)
        }
    }
    private tailrec fun reifyEvaluableByChar(
        char: Char?,
        quote: Boolean = false
    ): LispEvaluable? = when (char) {
        null -> null
        '"' -> {
            recurStringRest()
        }
        in '0'..'9' -> {
            recurIntRest(StringBuilder(char.toString()))
        }
        '\'' -> {
            if (quote) {
                throw LispParserEmptyQuoteException()
            } else {
                reifyEvaluableByChar(iterator.tryNext(), true)
            }
        }
        '(' -> {
            recurListRest()?.copy(quoted = quote) ?: LispNil
        }
        ' ', '\r', '\t', '\n' -> {
            if (quote) {
                throw LispParserEmptyQuoteException()
            }
            null
        }
        else -> recurSymbolRest(StringBuilder(char.toString())).copy(quoted = quote)
    }

    private tailrec fun recurStringRest(acc: StringBuilder = StringBuilder()): LispString = when (val char = iterator.tryNext()) {
        null -> throw LispParserUnclosedStringException()
        '\\' -> {
            val escaped = iterator.tryNext()?.let { "\\$it" }
                ?: throw LispParserUnclosedStringException()
            acc.append(escaped)
            recurStringRest(acc)
        }
        '"' -> LispString(acc.toString())
        else -> {
            acc.append(char)
            recurStringRest(acc)
        }
    }

    private tailrec fun recurSymbolRest(acc: StringBuilder): LispSymbol = when (iterator.tryPeek()) {
        null -> LispSymbol(acc.toString())
        '(', ')', '"' -> LispSymbol(acc.toString())
        ' ', '\r', '\t', '\n' -> LispSymbol(acc.toString())
        else -> {
            acc.append(iterator.next())
            recurSymbolRest(acc)
        }
    }

    private tailrec fun recurIntRest(acc: StringBuilder): LispInt = when (iterator.tryPeek()) {
        !in '0'..'9' -> {
            val convertedInt = acc.toString().toIntOrNull()
            if (convertedInt == null) {
                throw LispParserBadIntegerConversionException()
            }
            LispInt(convertedInt)
        }
        else -> {
            acc.append(iterator.next())
            recurIntRest(acc)
        }
    }

    private tailrec fun recurListRest(
        acc: List<LispEvaluable> = listOf(),
        dotOccurrence: Boolean = false,
        dotRightSide: LispEvaluable = LispNil
    ): LispConsCell? = when (val char = iterator.tryNext()) {
        null -> throw LispParserUnclosedListException()
        '.' -> {
            if (acc.isEmpty()) {
                throw LispParserDotWithoutLeftSideException()
            }
            recurListRest(acc, true, dotRightSide)
        }
        ')' -> {
            if (dotOccurrence && dotRightSide is LispNil) {
                throw LispParserDotWithoutRightSideException()
            }
            if (acc.isEmpty()) {
                null
            } else {
                acc.foldRight(dotRightSide) { first: LispEvaluable, rest: LispEvaluable ->
                    LispConsCell(first, rest)
                } as LispConsCell
            }
        }
        ' ', '\r', '\t', '\n' -> recurListRest(acc, dotOccurrence, dotRightSide)
        else -> {
            val nextEvaluable = reifyEvaluableByChar(char) ?: throw LispParserUnclosedListException()
            if (dotOccurrence) {
                recurListRest(acc, true, nextEvaluable)
            } else {
                recurListRest(acc + nextEvaluable, false, dotRightSide)
            }
        }
    }
}
