package org.sollecitom.exercises.sdk.api

import org.sollecitom.chassis.core.domain.identity.Id
import org.sollecitom.chassis.core.domain.traits.Identifiable

data class Vendor(override val id: Id) : Identifiable
