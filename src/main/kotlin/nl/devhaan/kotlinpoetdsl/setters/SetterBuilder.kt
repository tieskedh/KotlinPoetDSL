package nl.devhaan.kotlinpoetdsl.setters

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.asTypeName
import nl.devhaan.kotlinpoetdsl.IAccessor
import nl.devhaan.kotlinpoetdsl.PlainAccessor
import nl.devhaan.kotlinpoetdsl.codeblock.CodeBlockBuilder
import nl.devhaan.kotlinpoetdsl.helpers.FuncBlockWrapper

class SetterBuilder(
    private val accessor : IAccessor<*> = PlainAccessor(),
    private val callBack : (FunSpec) -> Unit
){

    operator fun invoke(codeBlock: CodeBlock) = initBuilder().addCode(codeBlock).build()

    operator fun invoke(buildScript: CodeBlockBuilder.() -> Unit): FunSpec {
        val builder = initBuilder()
        CodeBlockBuilder(builder).also(buildScript).build()
        return builder.build().also(callBack)
    }

    private fun initBuilder() = FuncBlockWrapper(FunSpec.setterBuilder().addParameter("value", Any::class.asTypeName()).addModifiers(*accessor.modifiers))

}