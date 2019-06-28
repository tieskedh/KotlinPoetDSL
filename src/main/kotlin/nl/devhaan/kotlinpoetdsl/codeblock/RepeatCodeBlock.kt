package nl.devhaan.kotlinpoetdsl.codeblock

import nl.devhaan.kotlinpoetdsl.CodeBlockLevel
import nl.devhaan.kotlinpoetdsl.helpers.BlockWrapper

private fun _createStartControllBlock(
        condition: String,
        vararg parts: Any,
        statements: CodeBlockBuilder.() -> Unit
) = createLazyComponent(true) { builder ->
    builder.beginControlFlow(condition, *parts)
    builder.addCode(statements)
    builder.endControlFlow()
}

private fun _forEach(
        condition: String,
        vararg parts: Any,
        statements: CodeBlockBuilder.() -> Unit
) = _createStartControllBlock(
        "for ($condition)",
        *parts,
        statements = statements
)

private fun _repeatWhile(
        condition: String,
        vararg parts: Any,
        statements: CodeBlockBuilder.() -> Unit
) = _createStartControllBlock(
        "while ($condition)",
        *parts,
        statements = statements
)

private fun _repeat(
        times: Int,
        arg: String,
        statements: CodeBlockBuilder.() -> Unit
) = _createStartControllBlock(
        "repeat($times) {" +
                " $arg ->".takeUnless { arg == "it" }.orEmpty(),
        statements = statements
)

class LazyRepeatCondition(val statements: CodeBlockBuilder.() -> Unit) {
    fun forEver() = asLongAs("true")
    infix fun asLongAs(condition: String) = asLongAs(condition, *emptyArray())
    fun asLongAs(condition: String, vararg parts: Any) = createLazyComponent(true) {
        it.beginControlFlow("do")
        it.addCode(statements)
        it.endControlFlow(" while ($condition)", *parts)
    }
}

interface IRepeat {
    fun forEach(condition: String, vararg parts: Any, statements: CodeBlockBuilder.() -> Unit)
    fun doRepeat(statements: CodeBlockBuilder.() -> Unit): RepeatStart.Condition
    fun repeatWhile(condition: String, vararg parts: Any, statements: CodeBlockBuilder.() -> Unit)
    fun repeat(times: Int, arg: String = "it", statements: CodeBlockBuilder.() -> Unit)
}

fun LazyComponentAcceptor.doRepeat(statements: CodeBlockBuilder.() -> Unit) = LazyRepeatCondition(statements)

fun LazyComponentAcceptor.forEach(
        condition: String,
        vararg parts: Any,
        statements: CodeBlockBuilder.() -> Unit
) = _forEach(condition, *parts, statements = statements)

fun LazyComponentAcceptor.repeatWhile(
        condition: String,
        vararg parts: Any,
        statements: CodeBlockBuilder.() -> Unit
) = _repeatWhile(condition, *parts, statements = statements)

fun LazyComponentAcceptor.repeat(
        times: Int, arg: String = "it", statements: CodeBlockBuilder.() -> Unit
) = _repeat(times, arg, statements)

@CodeBlockLevel
class RepeatStart(private val builder: BlockWrapper<*, *, *>) : IRepeat {
    override fun repeat(
            times: Int,
            arg: String,
            statements: CodeBlockBuilder.() -> Unit
    ) = _repeat(times, arg, statements)
            .wrapper(builder)

    override fun repeatWhile(
            condition: String,
            vararg parts: Any,
            statements: CodeBlockBuilder.() -> Unit
    ) = _repeatWhile(condition, *parts, statements = statements)
            .wrapper(builder)

    override fun forEach(
            condition: String,
            vararg parts: Any,
            statements: CodeBlockBuilder.() -> Unit
    ) = _forEach(condition, *parts, statements = statements)
            .wrapper(builder)

    override fun doRepeat(statements: CodeBlockBuilder.() -> Unit) =
            Condition(LazyRepeatCondition(statements))

    inner class Condition(private val lazyRepeatCondition: LazyRepeatCondition) {
        fun forEver() {
            lazyRepeatCondition.forEver()
                    .wrapper(builder)
        }

        infix fun asLongAs(condition: String) {
            lazyRepeatCondition.asLongAs(condition)
                    .wrapper(builder)
        }

        fun asLongAs(condition: String, vararg parts: Any) {
            lazyRepeatCondition.asLongAs(condition, *parts)
                    .wrapper(builder)
        }
    }
}