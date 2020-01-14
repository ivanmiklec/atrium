@file:Suppress("DEPRECATION" /* will be removed with 0.10.0 */)
@file:JvmMultifileClass
@file:JvmName("AnyAssertionsKt")
package ch.tutteli.atrium.api.cc.infix.en_GB

import ch.tutteli.atrium.creating.AssertionPlantNullable
import ch.tutteli.atrium.domain.builders.AssertImpl

@Deprecated("Use the extension function, will be removed with 0.10.0", ReplaceWith("plant toBe null"))
@Suppress("UNUSED_PARAMETER")
fun <T : Any?> toBe(plant: AssertionPlantNullable<T>, void: Void?) {
    plant.addAssertion(AssertImpl.any.toBeNull(plant))
}
