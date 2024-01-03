package org.sollecitom.exercises.sdk.test.specification

import org.sollecitom.exercises.sdk.api.SDK

interface TestSDK : SDK {

    fun newMarketPlace(): Marketplace
}