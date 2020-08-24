package nl.devhaan.kotlinpoetdsl.getters

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import nl.devhaan.kotlinpoetdsl.IAccessor
import nl.devhaan.kotlinpoetdsl.PlainAccessor
import nl.devhaan.kotlinpoetdsl.codeblock.CodeBlockBuildScript
import nl.devhaan.kotlinpoetdsl.helpers.FuncBlockWrapper

class GetterBuilder(
        private val accessor: IAccessor<*> = PlainAccessor(),
        private val callBack: (FunSpec) -> Unit
) {

    fun build(codeBlock: CodeBlock) = _build {
        addCode(codeBlock).build()
    }
    fun build(format: String, vararg args: Any) = _build {
        statement(format, *args).build()
    }

    fun build(buildScript: CodeBlockBuildScript) = _build {
        addCode(buildScript).build()
    }

    private fun _build(script: FuncBlockWrapper.() -> FunSpec) =
            initBuilder().let(script).also(callBack)

    private fun initBuilder() = FuncBlockWrapper(
            FunSpec.getterBuilder().addModifiers(*accessor.modifiers)
    )

}