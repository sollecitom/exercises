package org.sollecitom.exercises.sdk.api

interface User {

    val vendors: VendorOperations
}

interface VendorOperations {

    suspend fun query(maxPageSize: Int): Results<Vendor>
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