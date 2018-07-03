package nl.devhaan.kotlinpoetdsl.functions

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeName
import nl.devhaan.kotlinpoetdsl.IAccessor
import nl.devhaan.kotlinpoetdsl.Parameter
import nl.devhaan.kotlinpoetdsl.PlainAccessor
import nl.devhaan.kotlinpoetdsl.codeblock.CodeBlockBuilder
import nl.devhaan.kotlinpoetdsl.helpers.FuncBlockWrapper

class FuncBuilder(
        private val accessor: IAccessor<*> = PlainAccessor(),
        private val callBack: (FunSpec) -> Unit
) {
    private lateinit var builder: FuncBlockWrapper
    private val codeBlockBuilder get() = CodeBlockBuilder(builder)

    fun initBuilder(name: String) = FuncBlockWrapper(FunSpec.builder(name).addModifiers(*accessor.modifiers)).also {
        builder = it
    }


    operator fun invoke(name: String, vararg params: Parameter) = apply {
        builder = initBuilder(name).also {
            it.addParameters(params)
        }
    }

    fun buildReturn(typeName: TypeName, buildScript: CodeBlockBuilder.() -> Unit): FunSpec {
        builder.returns(typeName)
        codeBlockBuilder.let {
            buildScript(it)
            return@let it.build()
        }
        return builder.build().also(callBack)
    }

    internal fun build(builder: FunSpec.Builder, codeBlockBuildScript: CodeBlockBuilder.() -> Unit): FunSpec {
        this.builder = FuncBlockWrapper(builder)
        codeBlockBuilder.also(codeBlockBuildScript).build()
        return builder.build().also(callBack)
    }

    operator fun invoke(name: String, vararg params: Parameter, buildScript: CodeBlockBuilder.() -> Unit) = build(name, buildScript) {
        builder.addParameters(params)
    }

    private fun build(name: String, codeBlockBuildScript: CodeBlockBuilder.() -> Unit, buildScript: FuncBuilder.() -> Unit = {}): FunSpec {
        initBuilder(name)
        codeBlockBuilder.let {
            codeBlockBuildScript(it)
            buildScript(this)
            return@let it.build()
        }
        return builder.build().also(callBack)
    }

}

fun FunSpec.buildUpon(build: FunSpec.Builder.() -> Unit) = toBuilder().also(build).build()