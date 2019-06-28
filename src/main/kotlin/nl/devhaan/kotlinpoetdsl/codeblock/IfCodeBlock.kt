package nl.devhaan.kotlinpoetdsl.codeblock

import nl.devhaan.kotlinpoetdsl.codeblock.IfClassStart.IfClassEnd
import nl.devhaan.kotlinpoetdsl.helpers.BlockWrapper
import nl.devhaan.kotlinpoetdsl.helpers.UnFinishException
import java.util.LinkedList

private fun LazyComponentAcceptor._buildFirst(
        format: String,
        vararg parts: Any,
        statements: CodeBlockBuilder.() -> Unit
) = LazyIfClassEnd {
    this.builder.beginControlFlow(format, *parts)
    this.builder.addCode(statements)
}

fun LazyComponentAcceptor.If(
        format: String,
        vararg parts: Any,
        statements: CodeBlockBuilder.() -> Unit
) = _buildFirst("if ($format)", *parts, statements = statements)


fun LazyComponentAcceptor.ifp(
        format: String,
        vararg parts: Any,
        statements: CodeBlockBuilder.() -> Unit
) = _buildFirst("if (($format) == true)", *parts, statements = statements)

fun LazyComponentAcceptor.ifn(
        format: String,
        vararg parts: Any,
        statements: CodeBlockBuilder.() -> Unit
) = _buildFirst("if (($format) == false)", *parts, statements = statements)

class LazyIfClassEnd(statements: CodeBlockBuilder.() -> Unit){

    private val statementList = LinkedList<CodeBlockBuilder.()->Unit>().apply {
        add(statements)
    }

    fun end() = createLazyComponent(true){ builder ->
        statementList.forEach { builder.addCode(it) }
        builder.endControlFlow()
    }

    private fun addStatements(statementsToAdd: CodeBlockBuilder.()->Unit){
        statementList.add(statementsToAdd)
    }

    fun orElseIfn(format: String, vararg parts: Any, statements: CodeBlockBuilder.() -> Unit) =
        orElseIf("($format) == false", *parts, statements = statements)

    fun orElseIfp(format: String, vararg parts: Any, statements: CodeBlockBuilder.() -> Unit) =
            orElseIf("($format) == true", *parts, statements = statements)

    fun orElseIf(format: String, vararg parts: Any, statements: CodeBlockBuilder.() -> Unit): LazyIfClassEnd{
        addStatements{
            builder.nextControlFlow("else if ($format)", *parts)
            builder.addCode(statements)
        }
        return this
    }

    infix fun orElse(statements: CodeBlockBuilder.() -> Unit) : LazyComponent{
        addStatements {
            builder.nextControlFlow("else")
            builder.addCode(statements)
        }
        return end()
    }
}

interface IiFInterface {
    fun ifp(format: String, vararg parts: Any, statements: CodeBlockBuilder.() -> Unit) : IfClassEnd
    fun ifn(format: String, vararg parts: Any, statements: CodeBlockBuilder.() -> Unit) : IfClassEnd
    fun If(format: String, vararg parts: Any, statements: CodeBlockBuilder.() -> Unit) : IfClassEnd
}


class IfClassStart(private val builder: BlockWrapper<*, *, *>) : IiFInterface{
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
        builder.addCode(statements)
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

        fun orElseIf(format: String, vararg parts: Any, statements: CodeBlockBuilder.() -> Unit) = apply {
            builder.nextControlFlow("else if ($format)", *parts)
            buildSecond(statements)
        }

        private fun elseIf(shouldEqual: String, format: String, statements: CodeBlockBuilder.() -> Unit, vararg parts: Any) = apply {
            builder.nextControlFlow("else if (($format) == $shouldEqual)", *parts)
            buildSecond(statements)
        }

        infix fun orElse(statements: CodeBlockBuilder.() -> Unit) {
            builder.nextControlFlow("else")
            buildSecond(statements)
            end()
        }

        private fun buildSecond(statements: CodeBlockBuilder.() -> Unit) {
            builder.addCode(statements)
        }
    }
}