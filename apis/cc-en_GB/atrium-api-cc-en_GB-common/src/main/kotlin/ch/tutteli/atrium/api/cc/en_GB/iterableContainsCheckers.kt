@file:Suppress("DEPRECATION" /* will be removed with 0.10.0 */)
package ch.tutteli.atrium.api.cc.en_GB

import ch.tutteli.atrium.api.cc.en_GB.creating.iterable.contains.builders.*
import ch.tutteli.atrium.api.cc.en_GB.creating.iterable.contains.builders.impl.*
import ch.tutteli.atrium.domain.creating.iterable.contains.IterableContains
import ch.tutteli.atrium.domain.creating.iterable.contains.searchbehaviours.InAnyOrderSearchBehaviour

/**
 * Restricts a `contains` assertion by specifying that the number of occurrences of the entry which we are looking
 * for occurs `at least` number of [times] within the [Iterable].
 *
 * @param times The number which the check will compare against the actual number of times an expected entry is
 *   found in the [Iterable].
 *
 * @return The newly created builder.
 * @throws IllegalArgumentException In case [times] is smaller than zero.
 * @throws IllegalArgumentException In case [times] equals to zero; use [containsNot] instead.
 */
@Deprecated(
    "Switch from api-cc-en_GB to api-fluent-en_GB; will be removed with 0.10.0",
    ReplaceWith(
        "this.atLeast(times)",
        "ch.tutteli.atrium.api.fluent.en_GB.atLeast"
    )
)
fun <E, T : Iterable<E>, S: InAnyOrderSearchBehaviour> IterableContains.Builder<E, T, S>.atLeast(times: Int): AtLeastCheckerOption<E, T, S>
    = AtLeastCheckerOptionImpl(times, this)

/**
 * Restricts a `contains at least` assertion by specifying that the number of occurrences of the entry which we
 * are looking for occurs `at most` number of [times] within the [Iterable].
 *
 * The resulting restriction will be a `contains at least but at most` assertion.
 *
 * @param times The number which the check will compare against the actual number of times an expected entry is
 *   found in the [Iterable].
 *
 * @return The newly created builder.
 * @throws IllegalArgumentException In case [times] is smaller than zero.
 * @throws IllegalArgumentException In case [times] equals to zero; use [containsNot] instead.
 * @throws IllegalArgumentException In case [times] of this `at most` restriction equals to the number of the
 *   `at least` restriction; use the [exactly] restriction instead.
 */
@Deprecated(
    "Switch from api-cc-en_GB to api-fluent-en_GB; will be removed with 0.10.0",
    ReplaceWith(
        "this.butAtMost(times)",
        "ch.tutteli.atrium.api.fluent.en_GB.butAtMost"
    )
)
fun <E, T : Iterable<E>, S: InAnyOrderSearchBehaviour> AtLeastCheckerOption<E, T, S>.butAtMost(times: Int): ButAtMostCheckerOption<E, T, S>
    = ButAtMostCheckerOptionImpl(times, this, containsBuilder)

/**
 * Restricts a `contains` assertion by specifying that the number of occurrences of the entry which we
 * are looking for occurs `exactly` number of [times] within the [Iterable].
 *
 * @param times The number which the check will compare against the actual number of times an expected entry is
 *   found in the [Iterable].
 *
 * @return The newly created builder.
 * @throws IllegalArgumentException In case [times] is smaller than zero.
 * @throws IllegalArgumentException In case [times] equals to zero; use [containsNot] instead.
 */
@Deprecated(
    "Switch from api-cc-en_GB to api-fluent-en_GB; will be removed with 1.0.0",
    ReplaceWith(
        "this.exactly(times)",
        "ch.tutteli.atrium.api.fluent.en_GB.exactly"
    )
)
fun <E, T : Iterable<E>, S: InAnyOrderSearchBehaviour> IterableContains.Builder<E, T, S>.exactly(times: Int): ExactlyCheckerOption<E, T, S>
    = ExactlyCheckerOptionImpl(times, this)

/**
 * Restricts a `contains` assertion by specifying that the number of occurrences of the entry which we
 * are looking for occurs `at least` once but `at most` number of [times] within the [Iterable].
 *
 * If you want to use a higher lower bound than one, then use `atLeast(2).butAtMost(3)` instead of `atMost(3)`.
 * And in case you want to state that it is either not contained at all or at most a certain number of times,
 * then use `notOrAstMost(2)` instead.
 *
 * @param times The number which the check will compare against the actual number of times an expected entry is
 *   found in the [Iterable].
 *
 * @return The newly created builder.
 * @throws IllegalArgumentException In case [times] is smaller than zero.
 * @throws IllegalArgumentException In case [times] equals to zero; use [containsNot] instead.
 * @throws IllegalArgumentException In case [times] equals to one; use [exactly] instead.
 */
@Deprecated(
    "Switch from api-cc-en_GB to api-fluent-en_GB; will be removed with 1.0.0",
    ReplaceWith(
        "this.atMost(times)",
        "ch.tutteli.atrium.api.fluent.en_GB.atMost"
    )
)
fun <E, T : Iterable<E>, S: InAnyOrderSearchBehaviour> IterableContains.Builder<E, T, S>.atMost(times: Int): AtMostCheckerOption<E, T, S>
    = AtMostCheckerOptionImpl(times, this)

/**
 * Restricts a `contains` assertion by specifying that the number of occurrences of the entry which we
 * are looking for occurs `not at all or at most` number of [times] within the [Iterable].
 *
 * @param times The number which the check will compare against the actual number of times an expected entry is
 *   found in the [Iterable].
 *
 * @return The newly created builder.
 * @throws IllegalArgumentException In case [times] is smaller than zero.
 * @throws IllegalArgumentException In case [times] equals to zero; use [containsNot] instead.
 */
@Deprecated(
    "Switch from api-cc-en_GB to api-fluent-en_GB; will be removed with 1.0.0",
    ReplaceWith(
        "this.notOrAtMost(times)",
        "ch.tutteli.atrium.api.fluent.en_GB.notOrAtMost"
    )
)
fun <E, T : Iterable<E>, S: InAnyOrderSearchBehaviour> IterableContains.Builder<E, T, S>.notOrAtMost(times: Int): NotOrAtMostCheckerOption<E, T, S>
    = NotOrAtMostCheckerOptionImpl(times, this)
