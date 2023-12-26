package org.sollecitom.exercises.shopping_cart.one

import assertk.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.sollecitom.chassis.core.domain.currency.Currency
import org.sollecitom.chassis.core.domain.currency.SpecificCurrencyAmount
import org.sollecitom.chassis.core.domain.currency.known.Dollars
import org.sollecitom.chassis.core.domain.currency.known.currency
import org.sollecitom.chassis.core.domain.currency.known.toCurrencyAmount
import org.sollecitom.chassis.core.test.utils.isZero
import java.math.BigDecimal

@TestInstance(PER_CLASS)
private class ShoppingExampleTests {

    // TODO Tests
    // 1 banana
    // 2 bananas
    // 2 apples and 3 bananas
    // an unknown product
    // 3x2 discount
    // different prices on different days
    // a beer when the shopper is 14
    // a beer when the shopper is 25
    // different prices in different currencies across different shops

    @Test
    fun `checking out an empty cart`() = runTest {

        val shopper = newShopper()
        val shop = newShop<Dollars>()

        val bill = with(shop) {
            shopper.checkout()
        }

        assertThat(bill.total).isZero()
    }

    private inline fun <reified CURRENCY : SpecificCurrencyAmount<CURRENCY>> newShop(): Shop<CURRENCY> = InMemoryShop(CURRENCY::class.currency)

    private fun newShopper(): Shopper = InMemoryShopper()
}

internal class InMemoryShopper : Shopper {

    private val cart = InMemoryShoppingCart()

    context(Shop<CURRENCY>)
    override suspend fun <CURRENCY : SpecificCurrencyAmount<CURRENCY>> checkout(): Bill<CURRENCY> {

        return this@Shop.billFor(cart)
    }
}

data class InMemoryBill<CURRENCY : SpecificCurrencyAmount<CURRENCY>>(override val total: CURRENCY) : Bill<CURRENCY>

internal class InMemoryShop<CURRENCY : SpecificCurrencyAmount<CURRENCY>>(private val currency: Currency<CURRENCY>) : Shop<CURRENCY> {

    override suspend fun billFor(cart: ShoppingCart): Bill<CURRENCY> {

        val total = BigDecimal.ZERO.toCurrencyAmount(currency)
        return InMemoryBill(total)
    }
}

interface Shop<CURRENCY : SpecificCurrencyAmount<CURRENCY>> {

    suspend fun billFor(cart: ShoppingCart): Bill<CURRENCY>
}

interface ShoppingCart {

}

internal class InMemoryShoppingCart : ShoppingCart {

}

interface Shopper {

    context(Shop<CURRENCY>)
    suspend fun <CURRENCY : SpecificCurrencyAmount<CURRENCY>> checkout(): Bill<CURRENCY>
}

interface Bill<CURRENCY : SpecificCurrencyAmount<CURRENCY>> {

    val total: CURRENCY
}