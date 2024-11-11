package kotlisp

data class LispConsCell(
    val first: LispEvaluable,
    val rest: LispEvaluable,
    val quoted: Boolean = false
) : LispEvaluable() {
    override fun toString(): String = super.toString()
}

internal tailrec fun LispConsCell.toList(acc: MutableList<LispEvaluable> = mutableListOf()): List<LispEvaluable> =
    when (rest) {
        !is LispConsCell -> acc + first
        else -> {
            acc.add(first)
            rest.toList(acc)
        }
    }
