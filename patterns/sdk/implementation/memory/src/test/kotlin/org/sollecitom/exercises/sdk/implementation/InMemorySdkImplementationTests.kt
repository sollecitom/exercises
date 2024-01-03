package org.sollecitom.exercises.sdk.implementation

import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.sollecitom.exercises.sdk.api.User
import org.sollecitom.exercises.sdk.test.specification.SdkTestSpecification
import org.sollecitom.exercises.sdk.test.specification.TestSDK

@TestInstance(PER_CLASS)
private class InMemorySdkImplementationTests : SdkTestSpecification {

    override val sdk: TestSDK = InMemorySdk()
}

class InMemorySdk : TestSDK { // TODO move

    override fun newLoggedInUser(): User {
        TODO("Not yet implemented")
    }
}