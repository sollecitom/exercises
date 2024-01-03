package org.sollecitom.exercises.sdk.implementation

import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.sollecitom.chassis.core.test.utils.testProvider
import org.sollecitom.chassis.core.utils.CoreDataGenerator
import org.sollecitom.exercises.sdk.api.User
import org.sollecitom.exercises.sdk.api.Vendor
import org.sollecitom.exercises.sdk.api.VendorOperations
import org.sollecitom.exercises.sdk.test.specification.Marketplace
import org.sollecitom.exercises.sdk.test.specification.SdkTestSpecification
import org.sollecitom.exercises.sdk.test.specification.TestSDK

@TestInstance(PER_CLASS)
private class InMemorySdkImplementationTests : SdkTestSpecification, CoreDataGenerator by CoreDataGenerator.testProvider {

    override val sdk: TestSDK = InMemorySdk()
}

class InMemorySdk : TestSDK { // TODO move

    override fun newMarketPlace(initialVendors: Set<Vendor>): Marketplace = InMemoryMarketMarketplace(initialVendors)
}

internal class InMemoryMarketMarketplace(private val initialVendors: Set<Vendor>) : Marketplace {

    override fun newLoggedInUser() = InMemoryUser(allVendors = { initialVendors })
}

internal class InMemoryUser(private val allVendors: () -> Set<Vendor>) : User {

    override val vendors = InMemoryVendorOperations(allVendors)
}

internal class InMemoryVendorOperations(private val allVendors: () -> Set<Vendor>) : VendorOperations {

    override suspend fun query() = allVendors()
}
