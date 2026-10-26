import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.roundToLong

// A portable stand-in for String.format("%.${decimals}f"), which is JVM-only.
internal fun fmtDouble(value: Double, decimals: Int, width: Int = 0): String {
    // Check the sign bit rather than `value < 0`, so that -0.0 prints as "-0.00" the way %f does.
    val sign = if (value.toRawBits() < 0) "-" else ""
    val result = when {
        value.isNaN() -> "NaN"
        value.isInfinite() -> "${sign}Infinity"
        else -> {
            val factor = 10.0.pow(decimals)
            val rounded = (value.absoluteValue * factor).roundToLong()
            val intPart = rounded / factor.toLong()
            val fracPart = rounded % factor.toLong()
            "$sign$intPart.${fracPart.toString().padStart(decimals, '0')}"
        }
    }
    return if (width > 0) result.padStart(width) else result
}

internal fun fmtStr(value: Any, width: Int): String {
    val s = value.toString()
    return if (width < 0) s.padEnd(-width) else s.padStart(width)
}

internal fun fmtInt(value: Int, width: Int, padChar: Char = ' '): String =
    value.toString().padStart(width, padChar)
