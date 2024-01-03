package org.sollecitom.exercises.sdk.test.specification

import org.sollecitom.exercises.sdk.api.SDK
import org.sollecitom.exercises.sdk.api.Vendor

interface TestSDK : SDK {

    fun newMarketPlace(initialVendors: Set<Vendor>): Marketplace
}