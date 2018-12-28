package ch.tutteli.atrium.api.cc.en_GB

import ch.tutteli.atrium.assertions.Assertion
import ch.tutteli.atrium.creating.Assert
import ch.tutteli.atrium.creating.AssertionPlant
import ch.tutteli.atrium.domain.builders.AssertImpl

/**
 * Makes the assertion that [AssertionPlant.subject]'s [Map.size] is [size].
 *
 * @return This plant to support a fluent API.
 * @throws AssertionError Might throw an [AssertionError] if the assertion made is not correct.
 */
fun <T : Map<*, *>> Assert<T>.hasSize(size: Int)
    = addAssertion(AssertImpl.map.hasSize(this, size))

/**
 * Makes the assertion that [AssertionPlant.subject] is an empty [Map].
 *
 * @return This plant to support a fluent API.
 * @throws AssertionError Might throw an [AssertionError] if the assertion made is not correct.
 */
fun <T : Map<*, *>> Assert<T>.isEmpty()
    = addAssertion(AssertImpl.map.isEmpty(this))

/**
 * Makes the assertion that [AssertionPlant.subject] is not an empty [Map].
 *
 * @return This plant to support a fluent API.
 * @throws AssertionError Might throw an [AssertionError] if the assertion made is not correct.
 */
fun <T : Map<*, *>> Assert<T>.isNotEmpty()
    = addAssertion(AssertImpl.map.isNotEmpty(this))


/**
 * Creates an [AssertionPlant] for the [subject][AssertionPlant.subject]'s property [keys][Map.keys] so that further
 * fluent calls are assertions about it.
 *
 * Wrap it into Kotlin's [apply] if you want to make subsequent assertions on the current subject or use the overload
 * which expects an assertionCreator lambda where sub assertions are evaluated together (form an assertion group block).
 *
 * @return The newly created [AssertionPlant].
 */
val <K, V> Assert<Map<K, V>>.keys get() : Assert<Set<K>> = property(Map<K, V>::keys)

/**
 * Makes the assertion that [AssertionPlant.subject]'s property [keys][Map.keys] holds all assertions the given
 * [assertionCreator] might create.
 *
 * @return This plant to support a fluent API.
 * @throws AssertionError Might throw an [AssertionError] if a created [Assertion]s (by calling [assertionCreator])
 * does not hold.
 * @throws IllegalArgumentException in case the given [assertionCreator] did not create a single assertion.
 */
fun <K, V> Assert<Map<K, V>>.keys(assertionCreator: Assert<Set<K>>.() -> Unit): Assert<Map<K, V>> =
//TODO check that one assertion was created - problem property creates at least a feature assertion group, that's why collect is happy
    addAssertion(AssertImpl.collector.collect(this) {
        property(Map<K, V>::keys, assertionCreator)
    })

/**
 * Creates an [AssertionPlant] for the [subject][AssertionPlant.subject]'s property [values][Map.values] so that further
 * fluent calls are assertions about it.
 *
 * Wrap it into Kotlin's [apply] if you want to make subsequent assertions on the current subject or use the overload
 * which expects an assertionCreator lambda where sub assertions are evaluated together (form an assertion group block).
 *
 * @return The newly created [AssertionPlant].
 */
val <K, V> Assert<Map<K, V>>.values get() : Assert<Collection<V>> = property(Map<K, V>::values)

/**
 * Makes the assertion that [AssertionPlant.subject]'s property [values][Map.values] holds all assertions the given
 * [assertionCreator] might create.
 *
 * @return This plant to support a fluent API.
 * @throws AssertionError Might throw an [AssertionError] if a created [Assertion]s (by calling [assertionCreator])
 * does not hold.
 * @throws IllegalArgumentException in case the given [assertionCreator] did not create a single assertion.
 */
fun <K, V> Assert<Map<K, V>>.values(assertionCreator: Assert<Collection<V>>.() -> Unit): Assert<Map<K, V>> =
//TODO check that one assertion was created - problem property creates at least a feature assertion group, that's why collect is happy
    addAssertion(AssertImpl.collector.collect(this) {
        property(Map<K, V>::values, assertionCreator)
    })

/**
 * Turns `Assert<Map<K, V>>` into `Assert<Set<Map.Entry<K, V>>>`.
 *
 * The transformation as such is not reflected in reporting.
 * Use `property(subject::entries)` if you want to show the transformation in reporting.
 *
 * @return The newly created [AssertionPlant] for the transformed subject.
 */
fun <K, V> Assert<Map<K, V>>.asEntries(): Assert<Set<Map.Entry<K, V>>>
    = AssertImpl.changeSubject(this) { subject.entries }