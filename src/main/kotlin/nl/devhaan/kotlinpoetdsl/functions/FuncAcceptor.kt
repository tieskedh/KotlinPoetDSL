package nl.devhaan.kotlinpoetdsl.functions

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeName
import nl.devhaan.kotlinpoetdsl.codeblock.CodeBlockBuilder
import nl.devhaan.kotlinpoetdsl.IAccessor
import nl.devhaan.kotlinpoetdsl.Parameter
import nl.devhaan.kotlinpoetdsl.PlainAccessor
import kotlin.reflect.KClass

interface FunctionAcceptor {
    class TypeNameReturn(val clazz : TypeName, val builder: CodeBlockBuilder.() -> Unit)

    operator fun  <T: Any> KClass<T>.invoke(builder: CodeBlockBuilder.()->Unit) = this to builder
    operator fun  TypeName.invoke(builder: CodeBlockBuilder.()->Unit) = TypeNameReturn(this, builder)

    infix fun <T : Any> FuncBuilder.returns(pair: Pair<KClass<T>, CodeBlockBuilder.() -> Unit>): FunSpec {
        val (clazz, builder) = pair
        returns(clazz)
        return build(builder)
    }

    infix fun FuncBuilder.returns(typeNameReturn: TypeNameReturn): FunSpec {
        returns(typeNameReturn.clazz)
        return build(typeNameReturn.builder)
    }

    fun accept(func: FunSpec)
}

private fun FunctionAcceptor.funcBuilder() = FuncBuilder(
        accessor = this as? IAccessor<*> ?: PlainAccessor(),
        callBack = ::accept
)
fun FunctionAcceptor.func(funSpec: FunSpec) = accept(funSpec.let {
    if(this is IAccessor<*>){
        it.buildUpon { addModifiers(*this@func.modifiers) }
    } else it
})

fun FunctionAcceptor.func(name: String, buildScript: CodeBlockBuilder.() -> Unit) = funcBuilder()(name, buildScript)
fun FunctionAcceptor.func(name: String, vararg params: Parameter) = funcBuilder()(name, *params)
fun FunctionAcceptor.func(name: String, vararg params: Parameter, buildScript: CodeBlockBuilder.() -> Unit) =
        funcBuilder()(name, *params, buildScript = buildScript)


fun buildFun(vararg modifiers: KModifier, builder: FunctionAcceptor.()->FunSpec) = object : FunctionAcceptor {
    override fun accept(func: FunSpec) = Unit
}.let(builder).run {
    if (modifiers.isNotEmpty()) buildUpon { addModifiers(*modifiers) }
    else this
}