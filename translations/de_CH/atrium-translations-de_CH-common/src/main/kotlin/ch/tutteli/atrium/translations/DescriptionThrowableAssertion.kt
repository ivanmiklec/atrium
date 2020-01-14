package ch.tutteli.atrium.translations

import ch.tutteli.atrium.assertions.DescriptiveAssertion
import ch.tutteli.atrium.reporting.translating.StringBasedTranslatable

/**
 * Contains the [DescriptiveAssertion.description]s of the assertion functions which are applicable to [Throwable].
 */
enum class DescriptionThrowableAssertion(override val value: String) : StringBasedTranslatable {
    @Deprecated(
        "use DescriptionAnyAssertion.IS_A; will be removed with 0.10.0",
        ReplaceWith("ch.tutteli.atrium.translations.DescriptionAnyAssertion.IS_A")
    )
    IS_A("ist eine"),
    NO_EXCEPTION_OCCURRED("keine Exception wurde geworfen"),
    OCCURRED_EXCEPTION_CAUSE("cause"),
    OCCURRED_EXCEPTION_PROPERTIES("Eigenschaften der unerwarteten %s"),
    OCCURRED_EXCEPTION_MESSAGE("message"),
    OCCURRED_EXCEPTION_STACKTRACE("stacktrace"),
    OCCURRED_EXCEPTION_SUPPRESSED("suppressed"),
    IS_NOT_THROWN_1("wird"),
    IS_NOT_THROWN_2("nicht geworfen"),
}
