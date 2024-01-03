package org.sollecitom.exercises.sdk.test.specification

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.sollecitom.chassis.core.domain.identity.Id
import org.sollecitom.chassis.core.utils.CoreDataGenerator
import org.sollecitom.chassis.test.utils.assertions.containsSameElementsAs
import org.sollecitom.exercises.sdk.api.CollectionOperations
import org.sollecitom.exercises.sdk.api.User
import org.sollecitom.exercises.sdk.api.Vendor
import org.sollecitom.exercises.sdk.api.toList

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

        val created = user.collections.create()
        val collection = user.collections.withId(created.id)

        collection.delete()

        assertThat(user.collections.withId(created.id).exists()).isFalse()
    }

    private fun withUserAndMarketplace(initialVendors: List<Vendor> = (1..100).map { newVendor() }.toList(), action: suspend (User, Marketplace) -> Unit) = runTest {

        val marketplace = sdk.newMarketPlace(initialVendors)
        val user = marketplace.newLoggedInUser()
        action(user, marketplace)
    }

    private fun newVendor(id: Id = newId.internal()) = Vendor(id)

    private suspend fun CollectionOperations.create(vendors: List<Vendor> = emptyList()) = create(vendors = vendors, id = newId.internal())
}