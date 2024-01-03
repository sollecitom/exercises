package org.sollecitom.exercises.sdk.test.specification

import assertk.assertThat
import assertk.assertions.isFalse
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

// TODO move this whole file

interface SdkTestSpecification {

    val sdk: TestSDK

    @Test
    fun `not sure yet`() = runTest {

        val user = sdk.newLoggedInUser()

        assertThat(true).isFalse()
    }
}

