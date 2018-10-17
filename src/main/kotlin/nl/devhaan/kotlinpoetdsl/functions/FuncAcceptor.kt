package nl.devhaan.kotlinpoetdsl.functions

import com.squareup.kotlinpoet.*
import nl.devhaan.kotlinpoetdsl.*
import nl.devhaan.kotlinpoetdsl.ProvideBuilderAcceptor.ImplementationData
import nl.devhaan.kotlinpoetdsl.codeblock.CodeBlockBuilder
import kotlin.reflect.KClass

interface FunctionAcceptor {
    fun TypeName.func(name: String, vararg variables : Variable) = funcBuilder().startExtensionFunction(this, name, variables)
    fun KClass<*>.func(name: String, vararg variable: Variable) =  asTypeName().func(name, *variable)
    fun TypeName.func(name: String, vararg variables : Variable, buildScript: CodeBlockBuilder.() -> Unit) = funcBuilder().buildExtensionFunction(this, name, variables, buildScript = buildScript)
    fun KClass<*>.func(name: String, vararg variable: Variable, buildScript: CodeBlockBuilder.() -> Unit) =  asTypeName().func(name, *variable, buildScript = buildScript)

    fun Extension.func(name: String, vararg variables : Variable) = funcBuilder().startExtensionFunction(typeName, name, variables, modifiers)
    fun Extension.func(name: String, vararg variables : Variable, buildScript: CodeBlockBuilder.() -> Unit) =
            funcBuilder().buildExtensionFunction(this.typeName, name, variables, modifiers, buildScript)


    infix fun FuncBuilder.returns(name: TypeName) = buildReturn(name){}
    infix fun <T : Any> FuncBuilder.returns(name: KClass<T>) = buildReturn(name.asTypeName()){}
    infix fun FuncBuilder.returns(implementationData: ImplementationData<CodeBlockBuilder>): FunSpec =
            buildReturn(implementationData.typeName, implementationData.buildScript)

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


class Extension(val modifiers: Array<out KModifier>, val typeName: TypeName)
fun FunctionAcceptor.extension(typeName: TypeName) = Extension(
        modifiers = (this as? IAccessor<*>)?.modifiers.orEmpty(),
        typeName = typeName
)
fun FunctionAcceptor.extension(clazz: KClass<*>) = extension(clazz.asTypeName())



fun FunctionAcceptor.func(name: String, vararg variables: Variable) = funcBuilder()(name, *variables)
fun FunctionAcceptor.func(name: String, vararg variables: Variable, buildScript: CodeBlockBuilder.() -> Unit) =
        funcBuilder()(name, *variables, buildScript = buildScript)

fun FunctionAcceptor.func(name: String, param: ParameterSpec, vararg params: ParameterSpec) = funcBuilder() (name, param.toVariable(), *params.map{ it.toVariable() }.toTypedArray())
fun FunctionAcceptor.func(name: String, parameter: ParameterSpec, vararg params: ParameterSpec, buildScript: CodeBlockBuilder.() -> Unit) =
        funcBuilder()(name, parameter.toVariable(), *params.map { it.toVariable() }.toTypedArray(), buildScript = buildScript)