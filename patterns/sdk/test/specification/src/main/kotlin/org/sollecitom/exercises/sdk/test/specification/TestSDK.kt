package org.sollecitom.exercises.sdk.test.specification

import org.sollecitom.exercises.sdk.api.SDK
import org.sollecitom.exercises.sdk.api.User

interface TestSDK : SDK {

    fun newLoggedInUser(): User
}