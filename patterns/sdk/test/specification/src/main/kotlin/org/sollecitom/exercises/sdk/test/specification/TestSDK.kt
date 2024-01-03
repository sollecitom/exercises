package org.sollecitom.exercises.sdk.test.specification

import org.sollecitom.exercises.sdk.api.Vendor

interface TestSDK {

    fun newMarketPlace(initialVendors: List<Vendor>): Marketplace
}