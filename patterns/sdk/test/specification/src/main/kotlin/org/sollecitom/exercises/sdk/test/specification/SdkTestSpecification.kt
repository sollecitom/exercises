package org.sollecitom.exercises.sdk.test.specification

import assertk.assertThat
import assertk.assertions.doesNotContain
import assertk.assertions.isEqualTo
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.sollecitom.chassis.core.domain.identity.Id
import org.sollecitom.chassis.core.utils.CoreDataGenerator
import org.sollecitom.chassis.test.utils.assertions.containsSameElementsAs
import org.sollecitom.exercises.sdk.api.*

// TODO move this whole file

interface SdkTestSpecification : CoreDataGenerator {

    val sdk: TestSDK

    @Test
    fun `a user retrieves the vendors for a marketplace`() = runTest {

        val initialVendors = (1..10_000).map { newVendor() }.toList()
        val marketplace = sdk.newMarketPlace(initialVendors)
        val user = marketplace.newLoggedInUser()
        val maxPageSize = 100

        val vendors = user.vendors.query(maxPageSize = maxPageSize).toList()

        assertThat(vendors).containsSameElementsAs(initialVendors)
    }

    @Test
    fun `a user creates a collection of bookmarked`() = runTest {

        val initialVendors = (1..100).map { newVendor() }.toList()
        val marketplace = sdk.newMarketPlace(initialVendors)
        val user = marketplace.newLoggedInUser()
        val maxPageSize = 100
        val bookmarkedVendors = initialVendors.take(10)
        val collectionId = newId.internal()

        val collection = user.collections.create(id = collectionId, vendors = bookmarkedVendors)

        assertThat(collection.id).isEqualTo(collectionId)
        assertThat(collection.vendors(maxPageSize = maxPageSize).toList()).containsSameElementsAs(bookmarkedVendors)
    }

    @Test
    fun `a user deletes a collection of vendors`() = withUserAndMarketplace { user, marketplace ->

        val collection = user.collections.create()

        user.collections.delete(collection)

        assertThat(user.collections.query().toList()).doesNotContain(collection)
    }

    private fun withUserAndMarketplace(initialVendors: List<Vendor> = (1..100).map { newVendor() }.toList(), action: suspend (User, Marketplace) -> Unit) = runTest {

        val marketplace = sdk.newMarketPlace(initialVendors)
        val user = marketplace.newLoggedInUser()
        action(user, marketplace)
    }

    private suspend fun CollectionOperations.delete(collection: VendorCollection) = delete(collection.id)

    private fun newVendor(id: Id = newId.internal()) = Vendor(id)

    private suspend fun CollectionOperations.create(vendors: List<Vendor> = emptyList()) = create(vendors = vendors, id = newId.internal())

    private val defaultMaxPageSize get() = 100
    private fun CollectionOperations.query() = query(defaultMaxPageSize)
}