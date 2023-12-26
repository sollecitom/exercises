package org.sollecitom.exercises.shopping_cart.one

import assertk.assertThat
import assertk.assertions.isZero
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS

@TestInstance(PER_CLASS)
private class ShoppingExampleTests {

    // TODO Tests
    // empty cart
    // empty cart, with currency
    // 1 banana
    // 2 bananas
    // 2 apples and 3 bananas
    // 3x2 discount
    // different prices on different days
    // a beer when the shopper is 14
    // a beer when the shopper is 25
    // different prices in different currencies across different shops

    @Test
    fun `checking out an empty cart`() = runTest {

        val shopper = newShopper()
        val shop = newShop()

        val bill = with(shop) {
            shopper.checkout()
        }

        assertThat(bill.total).isZero()
    }

    private fun newShop(): Shop = InMemoryShop()

    private fun newShopper(): Shopper = InMemoryShopper()
}

internal class InMemoryShopper : Shopper {

    context(Shop)
    override suspend fun checkout(): Bill {

        return InMemoryBill(total = 0.0)
    }
}

data class InMemoryBill(override val total: Double) : Bill

internal class InMemoryShop : Shop {

}

interface Shop {

}

interface Shopper {

    context(Shop)
    suspend fun checkout(): Bill
}

interface Bill {

    val total: Double
}