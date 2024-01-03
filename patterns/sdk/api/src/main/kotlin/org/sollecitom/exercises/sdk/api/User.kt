package org.sollecitom.exercises.sdk.api

import org.sollecitom.chassis.core.domain.identity.Id
import org.sollecitom.chassis.core.domain.traits.Identifiable

interface User {

    val collections: CollectionOperations
    val vendors: VendorOperations
}

interface CollectionOperations {

    suspend fun create(id: Id, vendors: List<Vendor>): VendorCollection

    fun withId(id: Id): VendorCollection

    suspend fun delete(collectionId: Id)

    fun query(maxPageSize: Int): Results<VendorCollection>
}

interface VendorCollection : Identifiable {

    fun vendors(maxPageSize: Int): Results<Vendor>
}

interface VendorOperations {

    fun query(maxPageSize: Int): Results<Vendor>
}

interface Results<ENTITY : Any> {

    suspend fun next(): List<ENTITY>

    suspend fun hasNext(): Boolean
}

suspend fun <ENTITY : Any> Results<ENTITY>.toList(): List<ENTITY> {

    val all = mutableListOf<ENTITY>()
    while (hasNext()) {
        all += next()
    }
    return all
}