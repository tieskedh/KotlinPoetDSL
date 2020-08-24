package nl.devhaan.kotlinpoetdsl.functions

import com.squareup.kotlinpoet.*
import nl.devhaan.kotlinpoetdsl.*
import nl.devhaan.kotlinpoetdsl.ProvideBuilderAcceptor.ImplementationData
import nl.devhaan.kotlinpoetdsl.codeblock.CodeBlockBuildScript
import nl.devhaan.kotlinpoetdsl.codeblock.CodeBlockBuilder
import nl.devhaan.kotlinpoetdsl.codeblock.LazyComponent
import nl.devhaan.kotlinpoetdsl.codeblock.function.FunctionBlockBuildScript
import nl.devhaan.kotlinpoetdsl.codeblock.function.FunctionBlockBuilder
import nl.devhaan.kotlinpoetdsl.functions.FunctionAcceptor.IncompleteFunctionBuilder
import kotlin.reflect.KClass

interface FunctionAcceptor : IAcceptor {
    class IncompleteFunctionBuilder(val acceptor: FunctionAcceptor, val funcBuilder: FuncBuilder)

    // ---------------------- start incomplete
    fun KClass<*>.func(name: String, vararg variable: Variable) = asTypeName().func(name, *variable)
    fun TypeName.func(name: String, vararg variables: Variable) = incompleteFuncBuilder().unfinished {
        it.startExtensionFunction(this, name, variables)
    }


    fun Extension.func(name: String, vararg variables: Variable) = incompleteFuncBuilder().unfinished {
        it.startExtensionFunction(typeName, name, variables, modifiers)
    }


    // ---------------------- complete
    fun TypeName.func(name: String, vararg variables: Variable, buildScript: FunctionBlockBuildScript) =
            funcBuilder().buildExtensionFunction(this, name, variables, buildScript = buildScript)

    fun KClass<*>.func(name: String, vararg variable: Variable, buildScript: CodeBlockBuildScript) =
            asTypeName().func(name, *variable, buildScript = buildScript)


    fun Extension.func(name: String, vararg variables: Variable, buildScript: FunctionBlockBuildScript) =
            funcBuilder().buildExtensionFunction(this.typeName, name, variables, modifiers, buildScript)

    fun accept(func: FunSpec)

    fun FunSpec.attachFunc() = accept(this)
    fun FunSpec.attachFunc(editModifier: ModifierEditorDSL.() -> Set<KModifier>) {
        val newModifiers = ModifierEditorDSL(this.modifiers).editModifier()
        buildUpon {
            modifiers.clear()
            modifiers.addAll(newModifiers)
        }.attachFunc()
    }
}

private fun FunctionAcceptor.incompleteFuncBuilder() = IncompleteFunctionBuilder(this, funcBuilder().also(this::registerBuilder))
private fun FunctionAcceptor.funcBuilder() = FuncBuilder(
        accessor = this as? IAccessor<*> ?: PlainAccessor(),
        callBack = ::accept
)

//functions are private, such that they don't popup in the DSL
private inline fun IncompleteFunctionBuilder.unfinished(builder: (FuncBuilder) -> Unit) = apply {
    builder(funcBuilder)
}

private inline fun IncompleteFunctionBuilder.finished(builder: (FuncBuilder) -> FunSpec): FunSpec {
    acceptor.unregisterBuilder(funcBuilder)
    return builder(funcBuilder)
}

//------------------------------------------- start incomplete
fun FunctionAcceptor.func(name: String, vararg variables: Variable) = incompleteFuncBuilder().unfinished {
    it.initFunc(name, *variables)
}

fun FunctionAcceptor.func(name: String, param: ParameterSpec, vararg params: ParameterSpec) = incompleteFuncBuilder().unfinished { builder ->
    builder.initFunc(name, param.toVariable(), *params.map { it.toVariable() }.toTypedArray())
}

//------------------------------------------- start complete
fun FunctionAcceptor.func(name: String, vararg variables: Variable, buildScript: FunctionBlockBuildScript) =
        funcBuilder().buildReturn(name, *variables, buildScript = buildScript)

fun FunctionAcceptor.func(name: String, parameter: ParameterSpec, vararg params: ParameterSpec, buildScript: FunctionBlockBuildScript) =
        funcBuilder().buildReturn(name, parameter.toVariable(), *params.map { it.toVariable() }.toTypedArray(), buildScript = buildScript)


//------------------------------------------- only incomplete
infix fun IncompleteFunctionBuilder.returns(name: TypeName) = finished { it.buildReturn(name) {} }

infix fun <T : Any> IncompleteFunctionBuilder.returns(name: KClass<T>) = finished { it.buildReturn(name.asTypeName()) {} }

infix fun IncompleteFunctionBuilder.returns(implementationData: ImplementationData<FunctionBlockBuilder>) = finished {
    it.buildReturn(implementationData.typeName, implementationData.buildScript)
}

infix fun IncompleteFunctionBuilder.withBody(lazyComponent: LazyComponent) = finished { it.build(lazyComponent) }

//------------------------------------------ only complete

class Extension(val modifiers: Array<out KModifier>, val typeName: TypeName)

fun FunctionAcceptor.extension(typeName: TypeName) = Extension(
        modifiers = (this as? IAccessor<*>)?.modifiers.orEmpty(),
        typeName = typeName
)

fun FunctionAcceptor.extension(clazz: KClass<*>) = extension(clazz.asTypeName())
inline fun <reified T> FunctionAcceptor.extension() = extension(T::class.asTypeName())