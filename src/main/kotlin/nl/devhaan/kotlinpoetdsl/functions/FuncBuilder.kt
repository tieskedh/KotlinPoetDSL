package nl.devhaan.kotlinpoetdsl.functions

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeName
import nl.devhaan.kotlinpoetdsl.codeblock.CodeBlockBuilder
import nl.devhaan.kotlinpoetdsl.IAccessor
import nl.devhaan.kotlinpoetdsl.Parameter
import nl.devhaan.kotlinpoetdsl.PlainAccessor
import nl.devhaan.kotlinpoetdsl.helpers.FuncBlockWrapper
import kotlin.reflect.KClass

class FuncBuilder(
        private val accessor: IAccessor<*> = PlainAccessor(),
        private val callBack: (FunSpec) -> Unit
) {
    private lateinit var builder: FuncBlockWrapper
    private val codeBlockBuilder get() = CodeBlockBuilder(builder)

    operator fun invoke(name: String, vararg params: Parameter) = this.apply {
        builder = FuncBlockWrapper(FunSpec.builder(name).also {
            params.forEach { (name, type) ->
                it.addParameter(name, type.clazz, *type.modifiers.toTypedArray())
            }
            it.addModifiers(*accessor.modifiers)
        })
    }

    internal fun <T : Any> returns(clazz: KClass<T>) = builder.returns(clazz)
    internal fun returns(clazz: TypeName) = builder.returns(clazz)

    internal fun build(builder: FunSpec.Builder, codeBlockBuildScript: CodeBlockBuilder.() -> Unit): FunSpec {
        this.builder = FuncBlockWrapper(builder)
        codeBlockBuilder.also(codeBlockBuildScript).build()
        return builder.build().also(callBack)
    }

    operator fun invoke(name: String, buildScript: CodeBlockBuilder.() -> Unit) = build(name, buildScript)

    operator fun invoke(name: String, vararg params: Parameter, buildScript: CodeBlockBuilder.() -> Unit) = build(name, buildScript) {
        builder.apply {
            params.forEach { addParameter(it) }
        }
    }

    private fun build(name: String, codeBlockBuildScript: CodeBlockBuilder.() -> Unit, buildScript: FuncBuilder.() -> Unit = {}): FunSpec {
        builder = FuncBlockWrapper(FunSpec.builder(name).addModifiers(*accessor.modifiers))
        codeBlockBuilder.let {
            codeBlockBuildScript(it)
            buildScript(this)
            return@let it.build()
        }
        return builder.build().also(callBack)
    }

    fun build(builderScript: CodeBlockBuilder.() -> Unit): FunSpec {
        codeBlockBuilder.let {
            builderScript(it)
            return@let it.build()
        }
        return builder.build().also(callBack)
    }
}

fun FunSpec.buildUpon(build: FunSpec.Builder.()->Unit) = toBuilder().also(build).build()