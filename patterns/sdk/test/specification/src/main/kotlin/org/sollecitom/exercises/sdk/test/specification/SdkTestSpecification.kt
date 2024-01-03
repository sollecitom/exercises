package org.sollecitom.exercises.sdk.test.specification

import assertk.assertThat
import assertk.assertions.isFalse
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

// TODO move this whole file

interface SdkTestSpecification {

    val sdk: TestSDK

    @Test
    fun `a user retrieves the vendors for a marketplace`() = runTest {

        val marketplace = sdk.newMarketPlace()

        val user = marketplace.newLoggedInUser()

        val vendors = user.vendors.query()

        assertThat(true).isFalse()
    }
}