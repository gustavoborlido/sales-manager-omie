import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {
    private val brazilLocale = Locale("pt", "BR")
    private val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance(brazilLocale)

    fun formatCurrency(value: Double): String {
        return currencyFormat.format(value)
    }
}
