package nl.devhaan.kotlinpoetdsl.getters

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import nl.devhaan.kotlinpoetdsl.IAccessor
import nl.devhaan.kotlinpoetdsl.PlainAccessor
import nl.devhaan.kotlinpoetdsl.codeblock.CodeBlockBuilder
import nl.devhaan.kotlinpoetdsl.setters.SetterBuilder

interface GetterAcceptor {
    fun acceptGetter(func: FunSpec)
}

private fun GetterAcceptor.getterBuilder() = GetterBuilder(
        accessor = this as? IAccessor<*> ?: PlainAccessor(),
        callBack = ::acceptGetter
)

fun GetterAcceptor.getter(format: String, vararg values: Any?) = getterBuilder().addStatement(format, values)

fun GetterAcceptor.getter(codeBlock: CodeBlock) = getterBuilder() (codeBlock)

fun GetterAcceptor.getter(buildScript: CodeBlockBuilder.() -> Unit) = getterBuilder()(buildScript)