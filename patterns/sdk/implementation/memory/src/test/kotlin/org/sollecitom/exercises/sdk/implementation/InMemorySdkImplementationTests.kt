package org.sollecitom.exercises.sdk.implementation

import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.sollecitom.chassis.core.domain.identity.Id
import org.sollecitom.chassis.core.test.utils.testProvider
import org.sollecitom.chassis.core.utils.CoreDataGenerator
import org.sollecitom.exercises.sdk.api.*
import org.sollecitom.exercises.sdk.test.specification.Marketplace
import org.sollecitom.exercises.sdk.test.specification.SdkTestSpecification
import org.sollecitom.exercises.sdk.test.specification.TestSDK

@TestInstance(PER_CLASS)
private class InMemorySdkImplementationTests : SdkTestSpecification, CoreDataGenerator by CoreDataGenerator.testProvider {

    override val sdk: TestSDK = InMemorySdk()
}

class InMemorySdk : TestSDK { // TODO move

    override fun newMarketPlace(initialVendors: List<Vendor>): Marketplace = InMemoryMarketMarketplace(initialVendors)
}

internal class InMemoryMarketMarketplace(private val initialVendors: List<Vendor>) : Marketplace {

    override fun newLoggedInUser() = InMemoryUser(allVendors = { initialVendors })
}

internal class InMemoryUser(private val allVendors: () -> List<Vendor>) : User {

    override val vendors = InMemoryVendorOperations(allVendors)
    override val collections = InMemoryCollectionOperations()
}

internal class InMemoryCollectionOperations : CollectionOperations {

    override fun create(id: Id, vendors: List<Vendor>) = InMemoryCollection(id, vendors)
}

internal class InMemoryCollection(override val id: Id, private val vendors: List<Vendor>) : VendorCollection {

    override fun vendors(maxPageSize: Int) = InMemoryResults(maxPageSize) { vendors }
}

internal class InMemoryVendorOperations(private val allVendors: () -> List<Vendor>) : VendorOperations {

    override fun query(maxPageSize: Int) = InMemoryResults(maxPageSize, allVendors)
}

internal class InMemoryResults<ENTITY : Any>(private val maxPageSize: Int, private val allEntities: () -> List<ENTITY>) : Results<ENTITY> {

    private var store = allEntities()

    override suspend fun next(): List<ENTITY> = store.take(maxPageSize).also { store = store.drop(maxPageSize) }

    override suspend fun hasNext(): Boolean = store.isNotEmpty()
}
