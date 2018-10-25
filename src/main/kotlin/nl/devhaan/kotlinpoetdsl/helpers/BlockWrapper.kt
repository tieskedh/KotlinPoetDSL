package nl.devhaan.kotlinpoetdsl.helpers

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeName
import nl.devhaan.kotlinpoetdsl.Variable
import nl.devhaan.kotlinpoetdsl.codeblock.CodeBlockBuilder
import nl.devhaan.kotlinpoetdsl.endControlFlow

class UnFinishException(message: String) : Exception(message)
interface IFinishExceptionHandler {
    fun addUnFinishException(thrower: UnFinishException)
    fun removeUnFinishException(thrower: UnFinishException)
    fun finish()
}

class FinishExceptionHandler : IFinishExceptionHandler {
    private val finishExceptions = mutableListOf<UnFinishException>()
    override fun addUnFinishException(thrower: UnFinishException) {
        finishExceptions += thrower
    }

    override fun removeUnFinishException(thrower: UnFinishException) {
        finishExceptions -= thrower
    }

    override fun finish() {
        finishExceptions.firstOrNull()?.let { throw it }
    }
}

interface BlockWrapper<out RETURN, out SELF : BlockWrapper<RETURN, SELF, BUILDER>, out BUILDER> {
    fun withBuilder(buildScript: BUILDER.() -> Unit): SELF
    val finishHandler: FinishExceptionHandler
    fun statement(first: String, vararg parts: Any): SELF
    fun beginControlFlow(controlFlow: String, vararg args: Any): SELF
    fun nextControlFlow(controlFlow: String, vararg args: Any): SELF
    fun endControlFlow(format: String = "", vararg args: Any): SELF
    fun addCode(codeBlock: CodeBlock): SELF
    fun addCode(format: String, vararg varargs: Any): SELF
    fun build(): RETURN
    fun addCode(buildScript: CodeBlockBuilder.() -> Unit): SELF
}

fun FunSpec.Builder.wrapper() : BlockWrapper<FunSpec, FuncBlockWrapper, FunSpec.Builder> = FuncBlockWrapper(this)

fun CodeBlock.Builder.wrapper() : BlockWrapper<CodeBlock, CodeBlockWrapper, CodeBlock.Builder> = CodeBlockWrapper(this)
fun buildCodeBlock(builder: CodeBlockBuilder.() -> Unit): CodeBlock = CodeBlock.builder().wrapper().addCode(builder).build()

class CodeBlockWrapper(
        private val builder: CodeBlock.Builder = CodeBlock.builder()
) : BlockWrapper<CodeBlock, CodeBlockWrapper, CodeBlock.Builder> {
    override val finishHandler = FinishExceptionHandler()

    override fun addCode(buildScript: CodeBlockBuilder.() -> Unit) = apply {
        CodeBlockBuilder(this).also(buildScript)
    }

    override fun withBuilder(buildScript: CodeBlock.Builder.() -> Unit) = apply {
        builder.buildScript()
    }

    override fun addCode(format: String, vararg varargs: Any) = withBuilder { add(format, *varargs) }
    override fun addCode(codeBlock: CodeBlock) = withBuilder { add(codeBlock) }

    override fun beginControlFlow(controlFlow: String, vararg args: Any) =
            withBuilder { beginControlFlow(controlFlow, *args) }

    override fun nextControlFlow(controlFlow: String, vararg args: Any) =
            withBuilder { nextControlFlow(controlFlow, *args) }

    override fun endControlFlow(format: String, vararg args: Any) =
            withBuilder { endControlFlow(format, args) }
    override fun statement(first: String, vararg parts: Any) =
            withBuilder { addStatement(first, *parts) }
    override fun build(): CodeBlock {
        finishHandler.finish()
        return builder.build()
    }
}

class FuncBlockWrapper internal constructor(
        private val builder: FunSpec.Builder
) : BlockWrapper<FunSpec, FuncBlockWrapper, FunSpec.Builder> {
    override val finishHandler = FinishExceptionHandler()
    fun returns(clazz: TypeName) = withBuilder { returns(clazz) }


    override fun addCode(buildScript: CodeBlockBuilder.() -> Unit) = apply {
        CodeBlockBuilder(this).also(buildScript)
    }

    override fun withBuilder(buildScript: FunSpec.Builder.() -> Unit) = apply { builder.buildScript() }

    override fun addCode(codeBlock: CodeBlock) = withBuilder { addCode(codeBlock) }
    override fun addCode(format: String, vararg varargs: Any) = withBuilder { addCode(format, *varargs) }

    override fun statement(first: String, vararg parts: Any) = withBuilder { addStatement(first, *parts) }

    override fun beginControlFlow(controlFlow: String, vararg args: Any) = withBuilder { beginControlFlow(controlFlow, *args) }
    override fun nextControlFlow(controlFlow: String, vararg args: Any) = withBuilder { nextControlFlow(controlFlow, *args) }
    override fun endControlFlow(format: String, vararg args: Any) = withBuilder { endControlFlow(format, *args) }

    override fun build(): FunSpec {
        finishHandler.finish()
        return builder.build()
    }

    fun addParameters(parameters: Array<out Variable>) {
        builder.addParameters(parameters.map(Variable::toParamSpec))
    }

    fun receiver(typeName: TypeName) {
        builder.receiver(typeName)
    }

    fun addModifiers(modifiers: Array<out KModifier>) {
        builder.addModifiers(*modifiers)
    }
}