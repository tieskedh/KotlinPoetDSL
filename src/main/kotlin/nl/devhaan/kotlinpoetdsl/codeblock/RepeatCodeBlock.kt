package nl.devhaan.kotlinpoetdsl.codeblock

import nl.devhaan.kotlinpoetdsl.helpers.BlockWrapper


interface IRepeat {
    fun forEach(condition: String, vararg parts: Any, statements: CodeBlockBuilder.() -> Unit)
    fun doRepeat(statements: CodeBlockBuilder.() -> Unit): Repeat.Condition
    fun repeatWhile(condition: String, vararg parts: Any, statements: CodeBlockBuilder.() -> Unit)
}

open class Repeat(private val builder: BlockWrapper<*, *, *>) : IRepeat {
    override fun repeatWhile(condition: String, vararg parts: Any, statements: CodeBlockBuilder.() -> Unit) {
        builder.beginControlFlow("while ($condition)", *parts)
        endCodeBlock(statements)
    }
    override fun forEach(condition: String, vararg parts: Any, statements: CodeBlockBuilder.() -> Unit){
        builder.beginControlFlow("for ($condition)", *parts)
        endCodeBlock(statements)
    }

    private fun endCodeBlock(statements: CodeBlockBuilder.() -> Unit){
        builder.addCode(statements)
        builder.endControlFlow()
    }
    override fun doRepeat(statements: CodeBlockBuilder.() -> Unit) = Condition(statements)
    inner class Condition(private val statements: CodeBlockBuilder.() -> Unit) {
        fun forEver() = asLongAs("true")

        infix fun asLongAs(condition: String) = asLongAs(condition, *emptyArray())
        fun asLongAs(condition: String, vararg parts: Any){
            builder.beginControlFlow("do")
            builder.addCode(statements)
            builder.endControlFlow("while ($condition)", *parts)
        }
    }
}