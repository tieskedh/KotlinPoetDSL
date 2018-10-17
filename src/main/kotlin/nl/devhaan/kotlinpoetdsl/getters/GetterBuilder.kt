package nl.devhaan.kotlinpoetdsl.getters

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import nl.devhaan.kotlinpoetdsl.IAccessor
import nl.devhaan.kotlinpoetdsl.PlainAccessor
import nl.devhaan.kotlinpoetdsl.codeblock.CodeBlockBuilder
import nl.devhaan.kotlinpoetdsl.helpers.FuncBlockWrapper

class GetterBuilder(
        private val accessor: IAccessor<*> = PlainAccessor(),
        private val callBack: (FunSpec) -> Unit
) {

    operator fun invoke(codeBlock: CodeBlock) = build {
        addCode(codeBlock)
    }

    operator fun invoke(buildScript: CodeBlockBuilder.() -> Unit) = build {
        CodeBlockBuilder(this).also(buildScript).build()
    }

    private fun build(script: FuncBlockWrapper.() -> Unit) =
            initBuilder().also(script).build().also(callBack)

    private fun initBuilder() = FuncBlockWrapper(
            FunSpec.getterBuilder().addModifiers(*accessor.modifiers)
    )

    fun addStatement(format: String, values: Array<out Any?>) = build {
        statement(format, values)
    }

}