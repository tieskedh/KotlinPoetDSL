package nl.devhaan.kotlinpoetdsl.setters

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import nl.devhaan.kotlinpoetdsl.IAccessor
import nl.devhaan.kotlinpoetdsl.PlainAccessor
import nl.devhaan.kotlinpoetdsl.codeblock.CodeBlockBuildScript

interface SetterAcceptor{
    fun acceptSetter(func: FunSpec)
}

private fun SetterAcceptor.setterBuilder() = SetterBuilder(
        accessor = this as? IAccessor<*> ?: PlainAccessor(),
        callBack = ::acceptSetter
)

fun SetterAcceptor.setter(format: String, vararg values: Any?) = setterBuilder()(
        CodeBlock.of(format, *values)
)
fun SetterAcceptor.setter(codeBlock: CodeBlock) = setterBuilder()(codeBlock)
fun SetterAcceptor.setter(buildScript: CodeBlockBuildScript) = setterBuilder() (buildScript)