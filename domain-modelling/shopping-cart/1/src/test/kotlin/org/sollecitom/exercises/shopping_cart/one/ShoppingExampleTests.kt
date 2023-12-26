package org.sollecitom.exercises.shopping_cart.one

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.sollecitom.chassis.core.domain.currency.Currency
import org.sollecitom.chassis.core.domain.currency.SpecificCurrencyAmount
import org.sollecitom.chassis.core.domain.currency.known.Dollars
import org.sollecitom.chassis.core.domain.currency.known.cents
import org.sollecitom.chassis.core.domain.currency.known.currency
import org.sollecitom.chassis.core.domain.currency.known.toCurrencyAmount
import org.sollecitom.chassis.core.domain.currency.times
import org.sollecitom.chassis.core.domain.identity.Id
import org.sollecitom.chassis.core.domain.naming.Name
import org.sollecitom.chassis.core.domain.traits.Identifiable
import org.sollecitom.chassis.core.test.utils.isZero
import org.sollecitom.chassis.core.test.utils.testProvider
import org.sollecitom.chassis.core.utils.CoreDataGenerator
import java.math.BigDecimal

@TestInstance(PER_CLASS)
private class ShoppingExampleTests : CoreDataGenerator by CoreDataGenerator.testProvider {

    // TODO Tests
    // 2 bananas
    // 2 apples and 3 bananas
    // an unsupported product
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

    @Test
    fun `paying for some products`() = runTest {

        val shopper = newShopper()
        val banana = newProduct("Banana")
        val bananaPrice = 60.cents
        val shop = newShop<Dollars>(prices = mapOf(banana to bananaPrice))

        shopper.addToCart(banana)
        val bill = with(shop) {
            shopper.checkout()
        }

        assertThat(bill.total).isEqualTo(bananaPrice)
    }

    private fun newProduct(name: String, id: Id = newId.forEntities()): Product = ProductReference(id, name.let(::Name))

    private inline fun <reified CURRENCY : SpecificCurrencyAmount<CURRENCY>> newShop(prices: Map<Product, CURRENCY> = emptyMap()): Shop<CURRENCY> = InMemoryShop(CURRENCY::class.currency, prices)

    private fun newShopper(): Shopper = InMemoryShopper()
}

class ProductReference(override val id: Id, override val name: Name) : Product {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProductReference) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode() = id.hashCode()

    override fun toString() = "ProductReference(id=$id, name=$name)"
}

interface Product : Identifiable {

    val name: Name
}

internal class InMemoryShopper : Shopper {

    private val cart = InMemoryShoppingCart()

    context(Shop<CURRENCY>)
    override suspend fun <CURRENCY : SpecificCurrencyAmount<CURRENCY>> checkout(): Bill<CURRENCY> {

        return this@Shop.billFor(cart)
    }

    override suspend fun addToCart(product: Product) {

        cart.add(product)
    }
}

data class InMemoryBill<CURRENCY : SpecificCurrencyAmount<CURRENCY>>(override val total: CURRENCY) : Bill<CURRENCY>

internal class InMemoryShop<CURRENCY : SpecificCurrencyAmount<CURRENCY>>(private val currency: Currency<CURRENCY>, private val prices: Map<Product, CURRENCY>) : Shop<CURRENCY> {

    private val zero = BigDecimal.ZERO.toCurrencyAmount(currency)

    override suspend fun billFor(cart: ShoppingCart): Bill<CURRENCY> {

        val total = cart.items.values.map(::cost).fold(zero, SpecificCurrencyAmount<CURRENCY>::plus)
        return InMemoryBill(total)
    }

    private fun cost(item: ShoppingCart.Item): CURRENCY {

        val (product, quantity) = item
        val price = prices[product] ?: error("Unsupported product $product")
        return price * quantity
    }

}

interface Shop<CURRENCY : SpecificCurrencyAmount<CURRENCY>> {

    suspend fun billFor(cart: ShoppingCart): Bill<CURRENCY>
}

interface ShoppingCart {

    val items: Map<Product, Item>

    data class Item(val product: Product, val quantity: Int)
}

internal class InMemoryShoppingCart : ShoppingCart {

    private val _items = mutableMapOf<Product, Int>()
    override val items: Map<Product, ShoppingCart.Item> get() = _items.mapValues { it.asCartItem() }

    fun add(product: Product) {

        _items.compute(product) { _, quantity -> (quantity ?: 0) + 1 }
    }

    private fun Map.Entry<Product, Int>.asCartItem() = ShoppingCart.Item(key, value)
}

interface Shopper {

    context(Shop<CURRENCY>)
    suspend fun <CURRENCY : SpecificCurrencyAmount<CURRENCY>> checkout(): Bill<CURRENCY>

    suspend fun addToCart(product: Product)
}

interface Bill<CURRENCY : SpecificCurrencyAmount<CURRENCY>> {

    val total: CURRENCY
}