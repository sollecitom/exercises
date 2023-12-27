package org.sollecitom.exercises.shopping_cart.one

import assertk.Assert
import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.sollecitom.chassis.core.domain.age.Age
import org.sollecitom.chassis.core.domain.currency.Currency
import org.sollecitom.chassis.core.domain.currency.SpecificCurrencyAmount
import org.sollecitom.chassis.core.domain.currency.known.*
import org.sollecitom.chassis.core.domain.currency.times
import org.sollecitom.chassis.core.domain.identity.Id
import org.sollecitom.chassis.core.domain.naming.Name
import org.sollecitom.chassis.core.domain.traits.Identifiable
import org.sollecitom.chassis.core.test.utils.isZero
import org.sollecitom.chassis.core.test.utils.testProvider
import org.sollecitom.chassis.core.utils.CoreDataGenerator
import org.sollecitom.chassis.test.utils.assertions.failedThrowing
import org.sollecitom.chassis.test.utils.assertions.succeeded
import java.math.BigDecimal

@TestInstance(PER_CLASS)
private class ShoppingExampleTests : CoreDataGenerator by CoreDataGenerator.testProvider {

    // TODO Tests
    // breakdown by product quantity in the bill
    // 3x2 discount
    // different prices on different days
    // different prices in different currencies across different shops

    @Test
    fun `checking out an empty cart`() = runTest {

        val shopper = newShopper()
        val shop = newShop<Dollars>()

        val bill = shopper.checkout(shop)

        assertThat(bill.total).isZero()
    }

    @Test
    fun `paying for a product`() = runTest {

        val shopper = newShopper()
        val bananas = newProduct("Banana(s)")
        val bananaPrice = 60.cents
        val shop = newShop<Dollars>(prices = mapOf(bananas to bananaPrice))

        shopper.addToCart(bananas)
        val bill = shopper.checkout(shop)

        assertThat(bill.total).isEqualTo(bananaPrice)
    }

    @Test
    fun `paying for some products`() = runTest {

        val shopper = newShopper()
        val bananas = newProduct("Banana(s)")
        val apples = newProduct("Apple(s)")
        val bananaPrice = 50.pence
        val applePrice = 60.pence
        val shop = newShop<Pounds>(prices = mapOf(bananas to bananaPrice, apples to applePrice))

        shopper.addToCart(2 * bananas)
        shopper.addToCart(3 * apples)
        val bill = shopper.checkout(shop)

        assertThat(bill.total).isEqualTo(bananaPrice * 2 + applePrice * 3)
    }

    @Test
    fun `attempting to checkout a cart containing an unsupported product`() = runTest {

        val shopper = newShopper()
        val banana = newProduct("Banana(s)")
        val unsupportedProduct = newProduct("Unsupported product(s)")
        val bananaPrice = 60.cents
        val shop = newShop<Dollars>(prices = mapOf(banana to bananaPrice))

        shopper.addToCart(banana)
        shopper.addToCart(unsupportedProduct)

        val attempt = runCatching { shopper.checkout(shop) }

        assertThat(attempt).failedThrowing<UnsupportedProductException>().forProduct(unsupportedProduct)
    }

    @Test
    fun `attempting to checkout an age-restricted product while being underage`() = runTest {

        val shopper = newShopper(age = 14)
        val beer = newProduct(name = "Beer bottle(s)", requirements = setOf(minimumAge(value = 18)))
        val beerBottlePrice = 2.dollars
        val shop = newShop<Dollars>(prices = mapOf(beer to beerBottlePrice))

        shopper.addToCart(beer)

        val attempt = runCatching { shopper.checkout(shop) }

        assertThat(attempt).failedThrowing<UnmetProductRequirementsException>().forProduct(beer)
    }

    @Test
    fun `attempting to checkout an age-restricted product while not being underage`() = runTest {

        val shopper = newShopper(age = 18)
        val beer = newProduct(name = "Beer bottle(s)", requirements = setOf(minimumAge(value = 18)))
        val beerBottlePrice = 2.dollars
        val shop = newShop<Dollars>(prices = mapOf(beer to beerBottlePrice))

        shopper.addToCart(beer)

        val attempt = runCatching { shopper.checkout(shop) }

        assertThat(attempt).succeeded()
    }

    private fun <EXCEPTION : ProductException> Assert<EXCEPTION>.forProduct(product: Product) = given { error ->

        assertThat(error.product).isEqualTo(product)
    }

    private fun newProduct(name: String, id: Id = newId.forEntities(), requirements: Set<Product.Requirement> = emptySet()): Product = InMemoryProduct(id, name.let(::Name), Product.Restrictions.forRequirements(requirements))

    private inline fun <reified CURRENCY : SpecificCurrencyAmount<CURRENCY>> newShop(prices: Map<Product, CURRENCY> = emptyMap()): Shop<CURRENCY> = InMemoryShop(CURRENCY::class.currency, prices)

    private operator fun Int.times(product: Product) = ProductQuantity(product, this)

    private suspend fun Shopper.addToCart(product: Product) = addToCart(1 * product)

    private fun newShopper(age: Int = 50): Shopper = InMemoryShopper(details = ShopperDetails(dateOfBirth = clock.localDate - age.years))

    private fun minimumAge(value: Int) = MinimumAge(Age(value), clock)
}

data class MinimumAge(private val age: Age, private val clock: Clock) : Product.Requirement {

    override val description by lazy { "Minimum age requirement of $age".let(::Name) }

    override fun isMetBy(details: ShopperDetails): Boolean {

        val currentAge = details.dateOfBirth.yearsUntil(clock.localDate).let(::Age)
        return currentAge >= age
    }

    companion object
}

val Int.years: DatePeriod get() = DatePeriod(years = this)

fun Clock.localDateTime(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDateTime = now().toLocalDateTime(timeZone)

fun Clock.localDate(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDate = localDateTime(timeZone).date

fun Clock.localTime(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalTime = localDateTime(timeZone).time

val Clock.localDateTime: LocalDateTime get() = localDateTime()

val Clock.localDate: LocalDate get() = localDate()

val Clock.localTime: LocalTime get() = localTime()

data class ShopperDetails(val dateOfBirth: LocalDate)

class InMemoryProduct(override val id: Id, override val name: Name, private val restrictions: Product.Restrictions) : Product {

    override fun enforceRestrictions(details: ShopperDetails) = restrictions.enforce(details, this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is InMemoryProduct) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode() = id.hashCode()

    override fun toString() = "ProductReference(id=$id, name=$name)"
}

interface Product : Identifiable {

    val name: Name

    fun enforceRestrictions(details: ShopperDetails)

    interface Restrictions {

        fun enforce(details: ShopperDetails, product: Product)

        companion object
    }

    interface Requirement {

        val description: Name

        fun isMetBy(details: ShopperDetails): Boolean
    }
}

fun Product.Restrictions.Companion.forRequirements(requirements: Set<Product.Requirement>): Product.Restrictions = DelegatingProductRestrictions(requirements)

private class DelegatingProductRestrictions(private val requirements: Set<Product.Requirement>) : Product.Restrictions {

    override fun enforce(details: ShopperDetails, product: Product) {

        val unmetRequirements = requirements.filterNot { it.isMetBy(details) }.toSet()
        if (unmetRequirements.isNotEmpty()) throw UnmetProductRequirementsException(product, unmetRequirements)
    }
}

internal class InMemoryShopper(private val details: ShopperDetails) : Shopper {

    private val cart = InMemoryShoppingCart()

    override suspend fun <CURRENCY : SpecificCurrencyAmount<CURRENCY>> checkout(shop: Shop<CURRENCY>) = shop.billFor(cart = cart, details = details)

    override suspend fun addToCart(item: ProductQuantity) = cart.add(item)
}

data class InMemoryBill<CURRENCY : SpecificCurrencyAmount<CURRENCY>>(override val total: CURRENCY) : Bill<CURRENCY>

internal class InMemoryShop<CURRENCY : SpecificCurrencyAmount<CURRENCY>>(currency: Currency<CURRENCY>, private val prices: Map<Product, CURRENCY>) : Shop<CURRENCY> {

    private val zero = BigDecimal.ZERO.toCurrencyAmount(currency)

    override suspend fun billFor(cart: ShoppingCart, details: ShopperDetails): Bill<CURRENCY> {

        cart.enforceProductRestrictions(details)
        val total = cart.items.values.map { it.cost }.fold(zero, SpecificCurrencyAmount<CURRENCY>::plus)
        return InMemoryBill(total)
    }

    private fun ShoppingCart.enforceProductRestrictions(details: ShopperDetails) = items.keys.forEach { product -> product.enforceRestrictions(details) }

    private val ProductQuantity.cost: CURRENCY
        get() {
            val (product, quantity) = this
            val price = prices[product] ?: product.rejectAsUnsupported()
            return price * quantity
        }

    private fun Product.rejectAsUnsupported(): Nothing = throw UnsupportedProductException(this)
}

data class UnsupportedProductException(override val product: Product) : Exception("Unsupported product with ID '${product.id.stringValue}' and name ${product.name.value}"), ProductException

data class UnmetProductRequirementsException(override val product: Product, val unmetRequirements: Set<Product.Requirement>) : Exception("Restricted product with ID '${product.id.stringValue}' and name ${product.name.value} has unmet requirements: ${unmetRequirements.joinToString(separator = ", ") { it.description.value }}"), ProductException

sealed interface ProductException {

    val product: Product
}

interface Shop<CURRENCY : SpecificCurrencyAmount<CURRENCY>> {

    suspend fun billFor(cart: ShoppingCart, details: ShopperDetails): Bill<CURRENCY>
}

interface ShoppingCart {

    val items: Map<Product, ProductQuantity>
}

internal class InMemoryShoppingCart : ShoppingCart {

    private val _items = mutableMapOf<Product, Int>()
    override val items: Map<Product, ProductQuantity> get() = _items.mapValues { it.asProductQuantity() }

    fun add(item: ProductQuantity) {

        _items.compute(item.product) { _, quantity -> (quantity ?: 0) + item.quantity }
    }

    private fun Map.Entry<Product, Int>.asProductQuantity() = ProductQuantity(key, value)
}

interface Shopper {

    suspend fun <CURRENCY : SpecificCurrencyAmount<CURRENCY>> checkout(shop: Shop<CURRENCY>): Bill<CURRENCY>

    suspend fun addToCart(item: ProductQuantity)
}

data class ProductQuantity(val product: Product, val quantity: Int) {

    init {
        require(quantity > 0) { "Quantity must be greater than zero" }
    }
}

interface Bill<CURRENCY : SpecificCurrencyAmount<CURRENCY>> {

    val total: CURRENCY
}