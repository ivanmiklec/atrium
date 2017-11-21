package ch.tutteli.atrium.spec.assertions

import ch.tutteli.atrium.api.cc.en_UK.*
import ch.tutteli.atrium.assertions.DescriptionCharSequenceAssertion.AT_MOST
import ch.tutteli.atrium.creating.IAssertionPlant
import ch.tutteli.atrium.spec.IAssertionVerbFactory
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.include

abstract class CharSequenceContainsAtMostAssertionSpec(
    verbs: IAssertionVerbFactory,
    containsAtMostTriple: Triple<String, (String, String) -> String, IAssertionPlant<CharSequence>.(Int, Any, Array<out Any>) -> IAssertionPlant<CharSequence>>,
    containsAtMostIgnoringCaseTriple: Triple<String, (String, String) -> String, IAssertionPlant<CharSequence>.(Int, Any, Array<out Any>) -> IAssertionPlant<CharSequence>>,
    containsNotPair: Pair<String, (Int) -> String>,
    exactlyPair: Pair<String, (Int) -> String>
) : CharSequenceContainsSpecBase({

    include(object : ch.tutteli.atrium.spec.assertions.SubjectLessAssertionSpec<CharSequence>(
        containsAtMostTriple.first to mapToCreateAssertion { containsAtMostTriple.third(this, 2, 2.3, arrayOf()) },
        containsAtMostIgnoringCaseTriple.first to mapToCreateAssertion { containsAtMostIgnoringCaseTriple.third(this, 2, 2.3, arrayOf()) }
    ) {})

    include(object : ch.tutteli.atrium.spec.assertions.CheckingAssertionSpec<String>(verbs,
        checkingTriple(containsAtMostTriple.first, { containsAtMostTriple.third(this, 2, 2.3, arrayOf()) }, "2.3 / 2.3", "2.3 / 2.3 / 2.3"),
        checkingTriple(containsAtMostIgnoringCaseTriple.first, { containsAtMostIgnoringCaseTriple.third(this, 2, 2.3, arrayOf()) }, "2.3 / 2.3", "2.3 / 2.3 / 2.3")
    ) {})

    val assert: (CharSequence) -> IAssertionPlant<CharSequence> = verbs::checkImmediately
    val expect = verbs::checkException
    val fluent = assert(text)
    val fluentHelloWorld = assert(helloWorld)

    val (containsAtMost, containsAtMostTest, containsAtMostFunArr) = containsAtMostTriple
    fun IAssertionPlant<CharSequence>.containsAtMostFun(atLeast: Int, a: Any, vararg aX: Any)
        = containsAtMostFunArr(atLeast, a, aX)

    val (_, containsAtMostIgnoringCase, containsAtMostIgnoringCaseFunArr) = containsAtMostIgnoringCaseTriple
    fun IAssertionPlant<CharSequence>.containsAtMostIgnoringCaseFun(atLeast: Int, a: Any, vararg aX: Any)
        = containsAtMostIgnoringCaseFunArr(atLeast, a, aX)

    val (containsNot, errorMsgContainsNot) = containsNotPair
    val (exactly, errorMsgExactly) = exactlyPair

    describe("fun $containsAtMost") {

        context("throws an $illegalArgumentException") {
            test("for at most -1 -- only positive numbers") {
                expect {
                    fluent.containsAtMostFun(-1, "")
                }.toThrow<IllegalArgumentException> { message { contains("positive number", -1) } }
            }
            test("for at most 0 -- points to $containsNot") {
                expect {
                    fluent.containsAtMostFun(0, "")
                }.toThrow<IllegalArgumentException> { message { toBe(errorMsgContainsNot(0)) } }
            }
            test("for at most 1 -- points to $exactly") {
                expect {
                    fluent.containsAtMostFun(1, "")
                }.toThrow<IllegalArgumentException> { message { toBe(errorMsgExactly(1)) } }
            }
        }

        context("text '$helloWorld'") {
            group("happy case with $containsAtMost twice") {
                test("${containsAtMostTest("'H'", "once")} does not throw") {
                    fluentHelloWorld.containsAtMostFun(2, 'H')
                }
                test("${containsAtMostTest("'H' and 'e' and 'W'", "once")} does not throw") {
                    fluentHelloWorld.containsAtMostFun(2, 'H', 'e', 'W')
                }
                test("${containsAtMostTest("'W' and 'H' and 'e'", "once")} does not throw") {
                    fluentHelloWorld.containsAtMostFun(2, 'W', 'H', 'e')
                }
            }

            group("failing assertions; search string at different positions") {
                test("${containsAtMostTest("'l'", "twice")} throws AssertionError") {
                    expect {
                        fluentHelloWorld.containsAtMostFun(2, 'l')
                    }.toThrow<AssertionError> { message { containsDefaultTranslationOf(AT_MOST) } }
                }
                test("${containsAtMostTest("'H', 'l'", "twice")} throws AssertionError") {
                    expect {
                        fluentHelloWorld.containsAtMostFun(2, 'H', 'l')
                    }.toThrow<AssertionError> { message { contains(atMost, 'l') } }
                }
                test("${containsAtMostTest("'l', 'H'", "twice")} once throws AssertionError") {
                    expect {
                        fluentHelloWorld.containsAtMostFun(2, 'l', 'H')
                    }.toThrow<AssertionError> { message { contains(atMost, 'l') } }
                }
                test("${containsAtMostTest("'o', 'E', 'W', 'l'", "twice")} throws AssertionError") {
                    expect {
                        fluentHelloWorld.containsAtMostFun(2, 'o', 'E', 'W', 'l')
                    }.toThrow<AssertionError> { message { contains(atMost, 'o', 'l') } }
                }
                test("${containsAtMostTest("'x' and 'y' and 'z'", "twice")} throws AssertionError") {
                    expect {
                        fluentHelloWorld.containsAtMostFun(2, 'x', 'y', 'z')
                    }.toThrow<AssertionError> { message { contains(atLeast, 'x', 'y', 'z') } }
                }
            }

            group("multiple occurrences of the search string") {

                test("${containsAtMostTest("'o'", "twice")} does not throw") {
                    fluentHelloWorld.containsAtMostFun(2, 'o')
                }
                test("${containsAtMostIgnoringCase("'o'", "twice")} throws AssertionError and message contains both, how many times we expected (2) and how many times it actually contained 'o' ignoring case (3)") {
                    expect {
                        fluentHelloWorld.containsAtMostIgnoringCaseFun(2, 'o')
                    }.toThrow<AssertionError> {
                        message {
                            contains(
                                "$containsIgnoringCase: 'o'",
                                "$numberOfOccurrences: 3$separator"
                            )
                            endsWith("$atMost: 2")
                        }
                    }
                }

                test("${containsAtMostTest("'o'", "3 times")} does not throw") {
                    fluentHelloWorld.containsAtMostFun(3, 'o')
                }
                test("${containsAtMostTest("'o' and 'l'", "twice")} throws AssertionError and message contains both, how many times we expected (2) and how many times it actually contained 'l' (3)") {
                    expect {
                        fluentHelloWorld.containsAtMostFun(2, 'o', 'l')
                    }.toThrow<AssertionError> {
                        message {
                            contains(
                                "$containsDescr: 'l'",
                                "$numberOfOccurrences: 3$separator"
                            )
                            endsWith("$atMost: 2")
                            containsNot("$containsDescr 'o'")
                        }
                    }
                }
                test("${containsAtMostTest("'l'", "3 times")} does not throw") {
                    fluentHelloWorld.containsAtMostFun(3, 'l')
                }
                test("${containsAtMostTest("'o' and 'l'", "3 times")} does not throw") {
                    fluentHelloWorld.containsAtMostFun(3, 'o', 'l')
                }

            }
        }
    }
})