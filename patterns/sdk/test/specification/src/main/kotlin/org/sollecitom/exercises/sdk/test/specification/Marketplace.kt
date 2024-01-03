package org.sollecitom.exercises.sdk.test.specification

import org.sollecitom.exercises.sdk.api.User

interface Marketplace {

    fun newLoggedInUser(): User
}