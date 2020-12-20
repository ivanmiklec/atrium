package ch.tutteli.atrium.logic.creating.maplike.contains.creators.impl

import ch.tutteli.atrium.assertions.Assertion
import ch.tutteli.atrium.assertions.builders.assertionBuilder
import ch.tutteli.atrium.assertions.builders.invisibleGroup
import ch.tutteli.atrium.core.None
import ch.tutteli.atrium.core.Option
import ch.tutteli.atrium.core.Some
import ch.tutteli.atrium.core.coreFactory
import ch.tutteli.atrium.creating.AssertionContainer
import ch.tutteli.atrium.creating.Expect
import ch.tutteli.atrium.domain.creating.collectors.assertionCollector
import ch.tutteli.atrium.logic.*
import ch.tutteli.atrium.logic.assertions.impl.LazyThreadUnsafeAssertionGroup
import ch.tutteli.atrium.logic.creating.iterable.contains.creators.entriesInOrderOnly
import ch.tutteli.atrium.logic.creating.iterable.contains.creators.valuesInOrderOnly
import ch.tutteli.atrium.logic.creating.iterable.contains.steps.andOnly
import ch.tutteli.atrium.logic.creating.iterable.contains.steps.inOrder
import ch.tutteli.atrium.logic.creating.maplike.contains.MapLikeContains
import ch.tutteli.atrium.logic.creating.maplike.contains.creators.MapLikeContainsAssertions
import ch.tutteli.atrium.logic.creating.maplike.contains.searchbehaviours.InAnyOrderOnlySearchBehaviour
import ch.tutteli.atrium.logic.creating.maplike.contains.searchbehaviours.InAnyOrderSearchBehaviour
import ch.tutteli.atrium.logic.creating.maplike.contains.searchbehaviours.InOrderOnlySearchBehaviour
import ch.tutteli.atrium.logic.creating.typeutils.MapLike
import ch.tutteli.atrium.logic.utils.expectLambda
import ch.tutteli.atrium.reporting.MethodCallFormatter
import ch.tutteli.atrium.reporting.translating.TranslatableWithArgs
import ch.tutteli.atrium.translations.DescriptionIterableAssertion
import ch.tutteli.atrium.translations.DescriptionMapLikeAssertion
import ch.tutteli.kbox.ifWithinBound
import kotlin.reflect.KClass

class DefaultMapLikeContainsAssertions : MapLikeContainsAssertions {

    override fun <K, V, T : MapLike> keyValuePairsInAnyOrder(
        entryPointStepLogic: MapLikeContains.EntryPointStepLogic<K, V, T, InAnyOrderSearchBehaviour>,
        keyValuePairs: List<Pair<K, V>>
    ): Assertion = containsKeyWithValueAssertionInAnyOrder(
        entryPointStepLogic,
        turnKeyValuePairsToKeyWithValueAssertions(keyValuePairs)
    )

    private fun <K, V> turnKeyValuePairsToKeyWithValueAssertions(keyValuePairs: List<Pair<K, V>>) =
        keyValuePairs.map { (key, value) ->
            key to expectLambda<V> { _logicAppend { toBe(value) } }
        }


    override fun <K, V : Any, T : MapLike> keyWithValueAssertionsInAnyOrder(
        entryPointStepLogic: MapLikeContains.EntryPointStepLogic<K, out V?, T, InAnyOrderSearchBehaviour>,
        valueType: KClass<V>,
        keyValues: List<Pair<K, (Expect<V>.() -> Unit)?>>
    ): Assertion = containsKeyWithValueAssertionInAnyOrder(
        entryPointStepLogic,
        turnKeyWithNullableValueAssertionToKeyWithValueAssertion(keyValues)
    )

    private fun <K, V : Any> turnKeyWithNullableValueAssertionToKeyWithValueAssertion(keyValues: List<Pair<K, (Expect<V>.() -> Unit)?>>) =
        keyValues.map { (key, assertionCreatorOrNull) ->
            key to expectLambda<V?> { _logicAppend { silentToBeNullIfNullGivenElse(assertionCreatorOrNull) } }
        }


    //TODO 0.15.0: shouldn't we use this implementation instead of the current for toBeNullIfNullGivenElse?
    // we don't use toBeNullIfNullGivenElse because we want to avoid to show `is instance of`
    // alternatively we could also make it configurable if the check is included in reporting or not
    private fun <V : Any> AssertionContainer<V?>.silentToBeNullIfNullGivenElse(assertionCreatorOrNull: (Expect<V>.() -> Unit)?): Assertion =
        if (assertionCreatorOrNull == null) {
            toBeNull()
        } else {
            val assertion = assertionCollector.collect(maybeSubject.flatMap { if (it != null) Some(it) else None }) {
                addAssertionsCreatedBy(assertionCreatorOrNull)
            }
            maybeSubject.fold(
                {
                    // already in an explanatory assertion context, no need to wrap it again
                    assertion
                },
                {
                    if (it != null) {
                        assertion
                    } else {
                        assertionBuilder.explanatoryGroup
                            .withDefaultType
                            .withAssertion(assertion)
                            .failing
                            .build()
                    }
                }
            )
        }

    private fun <K, V, T : MapLike> containsKeyWithValueAssertionInAnyOrder(
        entryPointStepLogic: MapLikeContains.EntryPointStepLogic<K, out V, T, InAnyOrderSearchBehaviour>,
        keyValues: List<Pair<K, Expect<V>.() -> Unit>>
    ): Assertion {
        return LazyThreadUnsafeAssertionGroup {
            val map = convertToMap(entryPointStepLogic)
            val assertions = getFeatureAssertions(entryPointStepLogic, keyValues) { key ->
                extractKey(map, key)
            }
            assertionBuilder.list
                .withDescriptionAndEmptyRepresentation(
                    entryPointStepLogic.searchBehaviour.decorateDescription(
                        DescriptionMapLikeAssertion.CONTAINS
                    )
                )
                .withAssertions(assertions)
                .build()
        }
    }

    private fun <K, T : MapLike, V> convertToMap(
        entryPointStepLogic: MapLikeContains.EntryPointStepLogic<K, out V, T, *>
    ): Map<out K, V> =
        entryPointStepLogic.container.maybeSubject.fold(
            { emptyMap() },
            { entryPointStepLogic.converter(it) }
        )

    private fun <K, T : MapLike, V> getFeatureAssertions(
        entryPointStepLogic: MapLikeContains.EntryPointStepLogic<K, out V, T, *>,
        keyValues: List<Pair<K, Expect<V>.() -> Unit>>,
        extractor: (K) -> Option<V>
    ): List<Assertion> {
        val methodCallFormatter = getMethodCallFormatter(entryPointStepLogic)
        return keyValues.map { (key, assertionCreator) ->
            entryPointStepLogic.container.extractFeature
                .withDescription(entryWithKeyTranslation(methodCallFormatter, key))
                .withRepresentationForFailure(DescriptionMapLikeAssertion.KEY_DOES_NOT_EXIST)
                .withFeatureExtraction { extractor(key) }
                .withoutOptions()
                .build()
                .collect(assertionCreator)
        }

    }

    private fun <K> entryWithKeyTranslation(
        methodCallFormatter: MethodCallFormatter,
        key: K
    ) = TranslatableWithArgs(
        DescriptionMapLikeAssertion.ENTRY_WITH_KEY,
        methodCallFormatter.formatArgument(key)
    )

    private fun getMethodCallFormatter(
        @Suppress(/* don't suppress once we use MethodCallFormatter from container */ "UNUSED_PARAMETER")
        entryPointStepLogic: MapLikeContains.EntryPointStepLogic<*, *, *, *>
    ): MethodCallFormatter {
        //TODO we should actually make MethodCallFormatter configurable in ReporterBuilder and then get it via Expect
        return coreFactory.newMethodCallFormatter()
    }

    private fun <K, T : Map<out K, V>, V> extractKey(it: T, key: K): Option<V> {
        return Option.someIf(it.containsKey(key)) {
            @Suppress(
                "UNCHECKED_CAST"
                /*
                UNCHECKED_CAST is OK as this function will only be called if the key exists, so the value should be V
                One note though, if one deals with a Map returned by Java code and forgot that the Map actually contains
                `null` as values as well, then we ignore it here (due to the UNCHECKED_CAST). However, usually this
                should not matter as the assertion about the value will cover it. In the worst case, a null-check included
                by the Kotlin compiler will throw -> in such a case it might be hard for the user to grasp what is going on.
                In this sense it might be better if we catch that already here and report accordingly. Yet, in the end we
                end up introducing null-checks everywhere only because of Java => keep it like this for now.
                */
            )
            it[key] as V
        }
    }


    override fun <K, V, T : MapLike> keyValuePairsInAnyOrderOnly(
        entryPointStepLogic: MapLikeContains.EntryPointStepLogic<K, V, T, InAnyOrderOnlySearchBehaviour>,
        keyValuePairs: List<Pair<K, V>>
    ): Assertion = containsKeyWithValueAssertionInAnyOrderOnly(
        entryPointStepLogic,
        turnKeyValuePairsToKeyWithValueAssertions(keyValuePairs)
    )

    override fun <K, V : Any, T : MapLike> keyWithValueAssertionsInAnyOrderOnly(
        entryPointStepLogic: MapLikeContains.EntryPointStepLogic<K, out V?, T, InAnyOrderOnlySearchBehaviour>,
        valueType: KClass<V>,
        keyValues: List<Pair<K, (Expect<V>.() -> Unit)?>>
    ): Assertion = containsKeyWithValueAssertionInAnyOrderOnly(
        entryPointStepLogic,
        turnKeyWithNullableValueAssertionToKeyWithValueAssertion(keyValues)
    )

    private fun <K, V, T : MapLike> containsKeyWithValueAssertionInAnyOrderOnly(
        entryPointStepLogic: MapLikeContains.EntryPointStepLogic<K, out V, T, InAnyOrderOnlySearchBehaviour>,
        keyValues: List<Pair<K, Expect<V>.() -> Unit>>
    ): Assertion {
        return LazyThreadUnsafeAssertionGroup {
            val map = convertToMap(entryPointStepLogic)
            val mutableMap = map.toMutableMap()
            val featureAssertions = getFeatureAssertions(entryPointStepLogic, keyValues) { key ->
                val value = extractKey(mutableMap, key)
                mutableMap.remove(key)
                value
            }

            val hints = if (mutableMap.isNotEmpty()) {
                val additionalEntries = mutableMap.map { (key, value) ->
                    val description = entryWithKeyTranslation(getMethodCallFormatter(entryPointStepLogic), key)
                    assertionBuilder.descriptive
                        .holding
                        .withDescriptionAndRepresentation(description, value)
                        .build()
                }
                listOf(
                    assertionBuilder.explanatoryGroup
                        .withWarningType
                        .withAssertion(
                            assertionBuilder.list
                                .withDescriptionAndEmptyRepresentation(DescriptionMapLikeAssertion.WARNING_ADDITIONAL_ENTRIES)
                                .withAssertions(additionalEntries)
                                .build()
                        )
                        .build()
                )
            } else listOf<Assertion>()

            val assertions = featureAssertions + hints

            val description =
                entryPointStepLogic.searchBehaviour.decorateDescription(DescriptionMapLikeAssertion.CONTAINS)
            assertionBuilder.invisibleGroup.withAssertions(
                assertionCollector.collect(Some(map)) {
                    _logic.size().collectAndLogicAppend { toBe(keyValues.size) }
                },
                assertionBuilder.summary
                    .withDescription(description)
                    .withAssertions(assertions)
                    .build()
            ).build()
        }
    }

    override fun <K, V, T : MapLike> keyValuePairsInOrderOnly(
        entryPointStepLogic: MapLikeContains.EntryPointStepLogic<K, V, T, InOrderOnlySearchBehaviour>,
        keyValuePairs: List<Pair<K, V>>
    ): Assertion =
        entryPointStepLogic.container
            .builderContainsInIterableLike { convertToMap(entryPointStepLogic).entries }
            ._logic.inOrder._logic.andOnly._logic.entriesInOrderOnly(keyValuePairs.map { (key, value) ->
                expectLambda<Map.Entry<K, V>> { _logicAppend { isKeyValue(key, value) } }
            })

    override fun <K, V : Any, T : MapLike> keyWithValueAssertionsInOrderOnly(
        entryPointStepLogic: MapLikeContains.EntryPointStepLogic<K, out V?, T, InOrderOnlySearchBehaviour>,
        valueType: KClass<V>,
        keyValues: List<Pair<K, (Expect<V>.() -> Unit)?>>
    ): Assertion =
        entryPointStepLogic.container
            .builderContainsInIterableLike { convertToMap(entryPointStepLogic).entries }
            ._logic.inOrder._logic.andOnly._logic.entriesInOrderOnly(keyValues.map { (key, nullableAssertionCreator) ->
                expectLambda<Map.Entry<K, V?>> {
                    _logic.key().collectAndLogicAppend { toBe(key) }
                    _logic.value().collectAndLogicAppend { silentToBeNullIfNullGivenElse(nullableAssertionCreator) }
                }
            })
}
