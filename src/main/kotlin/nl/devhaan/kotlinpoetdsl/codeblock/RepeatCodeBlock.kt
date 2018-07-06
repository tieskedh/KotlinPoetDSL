package nl.devhaan.kotlinpoetdsl.codeblock

import nl.devhaan.kotlinpoetdsl.helpers.BlockWrapper

@Experimental(Experimental.Level.WARNING)
annotation class Uncertain

@Uncertain
interface IRepeat {
    fun repeat(condition: String, vararg parts: Any, statements: CodeBlockBuilder.() -> Unit)
    fun doRepeat(statements: CodeBlockBuilder.() -> Unit): Repeat.Condition
    fun init(string: String): Repeat
    fun repeat(from: Int, to: Int, index: String = "index", step: Int = 1, statements: CodeBlockBuilder.() -> Unit)
    fun repeat(range: IntRange, index: String = "index", statements: CodeBlockBuilder.() -> Unit) = repeat(range.first, range.last, index, range.step, statements)
    fun repeat(amount: Int, statements: CodeBlockBuilder.() -> Unit) = repeat(1, amount, "_",1,statements)
}


@Uncertain
open class Repeat(private val builder: BlockWrapper<*, *>) : IRepeat {
    override fun repeat(from: Int, to: Int, index: String, step: Int, statements: CodeBlockBuilder.()->Unit){
        if (step == 1) {
            if (from == 1 && index == "_"){
                builder.beginControlFlow("repeat(%L)", to)
            } else {
                builder.beginControlFlow("for(index in %L .. %L)", from, to)
            }
        } else {
            builder.beginControlFlow("for(index in %L .. %L step %L)", from, to, step)
        }
        endCodeBlock(statements)
    }

    override fun repeat(condition: String, vararg parts: Any, statements: CodeBlockBuilder.() -> Unit){
        builder.beginControlFlow("while($condition)", *parts)
        endCodeBlock(statements)
    }

    private fun endCodeBlock(statements: CodeBlockBuilder.() -> Unit){
        statements(CodeBlockBuilder(builder))
        builder.endControlFlow()
    }
    override fun doRepeat(statements: CodeBlockBuilder.() -> Unit) = Condition(statements)
    inner class Condition(private val statements: CodeBlockBuilder.() -> Unit) {
        fun forEver() {
            this@Repeat.repeat(condition = "true", parts = *arrayOf(), statements = statements)
        }

        fun asLongAs(condition: String, vararg parts: Any){
            builder.beginControlFlow("do")
            statements(CodeBlockBuilder(builder))
            builder.endControlFlow()
            builder.statement("while($condition)", *parts)
        }
    }

    override fun init(string: String)= Repeat(builder)
}