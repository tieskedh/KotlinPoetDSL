package nl.devhaan.kotlinpoetdsl.functions

import com.squareup.kotlinpoet.*
import nl.devhaan.kotlinpoetdsl.IAccessor
import nl.devhaan.kotlinpoetdsl.PlainAccessor
import nl.devhaan.kotlinpoetdsl.Variable
import nl.devhaan.kotlinpoetdsl.codeblock.CodeBlockBuilder
import nl.devhaan.kotlinpoetdsl.toVariable
import kotlin.reflect.KClass

interface FunctionAcceptor {
    operator fun <T : Any> KClass<T>.invoke(builder: CodeBlockBuilder.() -> Unit) = this.asTypeName() to builder
    operator fun TypeName.invoke(builder: CodeBlockBuilder.() -> Unit) = this to builder

    infix fun FuncBuilder.returns(typeNameReturn: Pair<TypeName, CodeBlockBuilder.() -> Unit>): FunSpec =
            buildReturn(typeNameReturn.first, typeNameReturn.second)

    fun accept(func: FunSpec)
}

private fun FunctionAcceptor.funcBuilder() = FuncBuilder(
        accessor = this as? IAccessor<*> ?: PlainAccessor(),
        callBack = ::accept
)

fun FunctionAcceptor.func(funSpec: FunSpec) = accept(funSpec.let {
    if (this is IAccessor<*>) {
        it.buildUpon { addModifiers(*this@func.modifiers) }
    } else it
})


fun FunctionAcceptor.func(name: String, vararg variables: Variable) = funcBuilder()(name, *variables)
fun FunctionAcceptor.func(name: String, vararg variables: Variable, buildScript: CodeBlockBuilder.() -> Unit) =
        funcBuilder()(name, *variables, buildScript = buildScript)

fun FunctionAcceptor.func(name: String, param: ParameterSpec, vararg params: ParameterSpec) = funcBuilder()(name, param.toVariable(), *params.map{ it.toVariable() }.toTypedArray())
fun FunctionAcceptor.func(name: String, parameter: ParameterSpec, vararg params: ParameterSpec, buildScript: CodeBlockBuilder.() -> Unit) =
        funcBuilder()(name, parameter.toVariable(), *params.map { it.toVariable() }.toTypedArray(), buildScript = buildScript)


fun buildFun(vararg modifiers: KModifier, builder: FunctionAcceptor.() -> FunSpec) = object : FunctionAcceptor {
    override fun accept(func: FunSpec) = Unit
}.let(builder).run {
    if (modifiers.isNotEmpty()) buildUpon { addModifiers(*modifiers) }
    else this
}