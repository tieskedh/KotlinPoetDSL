package nl.devhaan.kotlinpoetdsl.setters

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.asTypeName
import nl.devhaan.kotlinpoetdsl.IAccessor
import nl.devhaan.kotlinpoetdsl.PlainAccessor
import nl.devhaan.kotlinpoetdsl.codeblock.CodeBlockBuilder
import nl.devhaan.kotlinpoetdsl.helpers.BlockWrapper
import nl.devhaan.kotlinpoetdsl.helpers.wrapper

class SetterBuilder(
    private val accessor : IAccessor<*> = PlainAccessor(),
    private val callBack : (FunSpec) -> Unit
){

    operator fun invoke(codeBlock: CodeBlock) = build { addCode(codeBlock) }

    operator fun invoke(buildScript: CodeBlockBuilder.() -> Unit) = build {
        addCode(buildScript)
    }

    private fun build(buildScript: BlockWrapper<FunSpec, *, *>.()->Unit) =
            FunSpec.setterBuilder()
                    .addParameter("value", Any::class.asTypeName())
                    .addModifiers(*accessor.modifiers)
                    .wrapper()
                    .also(buildScript)
                    .build()
                    .also(callBack)
}