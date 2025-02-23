import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object CurrencyUtils {
    fun formatCurrency(value: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("pt", "BR")) as DecimalFormat
        format.currency = Currency.getInstance("BRL")
        return format.format(value)
    }

    fun parseCurrencyToDouble(value: String): Double {
        val cleanValue = value.replace(Regex("[^0-9]"), "").toDoubleOrNull() ?: 0.0
        return cleanValue / 100
    }

    fun createCurrencyVisualTransformation(): VisualTransformation {
        return VisualTransformation { text ->
            val parsedValue = parseCurrencyToDouble(text.text)
            val formatted = formatCurrency(parsedValue)

            val offsetMapping = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int = formatted.length
                override fun transformedToOriginal(offset: Int): Int = text.text.length
            }

            TransformedText(AnnotatedString(formatted), offsetMapping)
        }
    }
}
