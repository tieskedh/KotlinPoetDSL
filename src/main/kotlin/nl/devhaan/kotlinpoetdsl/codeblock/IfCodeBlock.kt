package nl.devhaan.kotlinpoetdsl.codeblock

import nl.devhaan.kotlinpoetdsl.codeblock.IfClassStart.IfClassEnd
import nl.devhaan.kotlinpoetdsl.helpers.BlockWrapper
import nl.devhaan.kotlinpoetdsl.helpers.UnFinishException

interface IiFInterface {
    fun ifp(format: String, vararg parts: Any, statements: CodeBlockBuilder.() -> Unit) : IfClassEnd
    fun ifn(format: String, vararg parts: Any, statements: CodeBlockBuilder.() -> Unit) : IfClassEnd
    fun If(format: String, vararg parts: Any, statements: CodeBlockBuilder.() -> Unit) : IfClassEnd
}


class IfClassStart(private val builder: BlockWrapper<*, *>) : IiFInterface{
    override fun ifp(format: String, vararg parts: Any, statements: CodeBlockBuilder.() -> Unit)
            = buildFirst(statements, "if (($format) == true)", *parts)

    override fun ifn(format: String, vararg parts: Any, statements: CodeBlockBuilder.() -> Unit) =
            buildFirst(statements,"if (($format) == false)", *parts)

    override fun If(format: String, vararg parts: Any, statements: CodeBlockBuilder.() -> Unit)
            = buildFirst(statements,"if ($format)", *parts)

    private fun buildFirst(statements: CodeBlockBuilder.() -> Unit, format: String, vararg parts: Any): IfClassEnd {
        val exception = UnFinishException("ifStatement not finished")
        builder.finishHandler.addUnFinishException(exception)
        builder.beginControlFlow(format, *parts)
        statements(CodeBlockBuilder(builder))
        return IfClassEnd(exception)
    }

    inner class IfClassEnd(private val exception: UnFinishException) {
        fun end() {
            builder.finishHandler.removeUnFinishException(exception)
            builder.endControlFlow()
        }

        fun orElseIfp(format: String, vararg parts: Any, statements: CodeBlockBuilder.() -> Unit)
                = elseIf("true", format, statements, *parts)

        fun orElseIfn(format: String, vararg parts: Any, statements: CodeBlockBuilder.() -> Unit)
                = elseIf("false", format, statements, *parts)

        fun orElseIf(format: String, vararg parts: Any, statements: CodeBlockBuilder.() -> Unit): IfClassEnd {
            builder.nextControlFlow("else if ($format)", *parts)
            buildSecond(statements)
            return this
        }

        private fun elseIf(shouldEqual: String, format: String, statements: CodeBlockBuilder.() -> Unit, vararg parts: Any): IfClassEnd {
            builder.nextControlFlow("else if (($format) == $shouldEqual)", *parts)
            buildSecond(statements)
            return this
        }

        infix fun orElse(statements: CodeBlockBuilder.() -> Unit) {
            builder.nextControlFlow("else")
            buildSecond(statements)
            end()
        }

        private fun buildSecond(statements: CodeBlockBuilder.() -> Unit) {
            statements(CodeBlockBuilder(builder))
        }
    }
}