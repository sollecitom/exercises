package org.sollecitom.exercises.sdk.test.specification

import assertk.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.sollecitom.chassis.core.domain.identity.Id
import org.sollecitom.chassis.core.utils.CoreDataGenerator
import org.sollecitom.chassis.test.utils.assertions.containsSameElementsAs
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

    private fun newVendor(id: Id = newId.internal()) = Vendor(id)
}