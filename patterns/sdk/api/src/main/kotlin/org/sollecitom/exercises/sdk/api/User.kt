package org.sollecitom.exercises.sdk.api

interface User {

    val vendors: VendorOperations
}

interface VendorOperations {

    suspend fun query(): Set<Vendor>
}
