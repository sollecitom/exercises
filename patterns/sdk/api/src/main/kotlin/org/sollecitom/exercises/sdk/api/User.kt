package org.sollecitom.exercises.sdk.api

interface User {

    val vendors: VendorOperations
}

interface VendorOperations {

    suspend fun query(maxPageSize: Int): Results<Vendor>
}

interface Results<ENTITY : Any> {

    fun next(): List<ENTITY>

    fun hasNext(): Boolean
}