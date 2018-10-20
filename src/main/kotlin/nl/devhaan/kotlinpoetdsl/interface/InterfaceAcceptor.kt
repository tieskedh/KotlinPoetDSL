package nl.devhaan.kotlinpoetdsl.`interface`

import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import nl.devhaan.kotlinpoetdsl.IAcceptor
import nl.devhaan.kotlinpoetdsl.IAccessor
import nl.devhaan.kotlinpoetdsl.PlainAccessor
import nl.devhaan.kotlinpoetdsl.ProvideBuilderAcceptor.ImplementationData
import nl.devhaan.kotlinpoetdsl.`interface`.InterfaceAcceptor.IncompleteInterfBuilder
import nl.devhaan.kotlinpoetdsl.classes.buildUpon
import kotlin.reflect.KClass


interface InterfaceAcceptor : IAcceptor {
    class IncompleteInterfBuilder(val acceptor: InterfaceAcceptor, val interfBuilder: InterfaceBuilder)
    fun accept(type: TypeSpec)
}


//functions are private, such that they don't popup in the DSL
private inline fun IncompleteInterfBuilder.unFinished(buildScript: InterfaceBuilder.() -> Unit)
        = apply { buildScript(interfBuilder) }
private inline fun IncompleteInterfBuilder.finished(buildScript: InterfaceBuilder.() -> TypeSpec): TypeSpec {
    acceptor.unregisterBuilder(interfBuilder)
    return buildScript(interfBuilder)
}
private fun InterfaceAcceptor.incompleteBuilder() = IncompleteInterfBuilder(this, interfBuilder().also(this::registerBuilder))
private fun InterfaceAcceptor.interfBuilder() = InterfaceBuilder(
        accessor = this as? IAccessor<*> ?: PlainAccessor(),
        adding = ::accept
)


//----------------------------------------------- only incomplete
fun IncompleteInterfBuilder.implements(vararg typeNames: TypeName, buildScript: InterfaceBuilder.() -> Unit) =
        finished { implements(*typeNames).finishBuild(buildScript) }
fun IncompleteInterfBuilder.implements(vararg clazzes: KClass<*>, buildScript: InterfaceBuilder.() -> Unit) =
        finished { implements(*clazzes).finishBuild(buildScript) }

infix fun IncompleteInterfBuilder.implements(implementationData: ImplementationData<InterfaceBuilder>) =
        finished { finishBuild(implementationData) }

//----------------------------------------------- complete and incomplete
fun IncompleteInterfBuilder.implements(vararg clazzes: KClass<*>) = unFinished { implements(*clazzes) }
fun InterfaceBuilder.implements(vararg clazzes: KClass<*>) = apply {
    clazzes.forEach { addImplement(it.asTypeName()) }
}


fun IncompleteInterfBuilder.implements(vararg typeNames: TypeName) = unFinished { implements(*typeNames) }
fun InterfaceBuilder.implements(vararg typeNames: TypeName) = apply {
    typeNames.forEach { addImplement(it) }
}


infix fun IncompleteInterfBuilder.implements(typeName: TypeName) = unFinished { addImplement(typeName) }
infix fun InterfaceBuilder.implements(typeName: TypeName) = addImplement(typeName)


infix fun IncompleteInterfBuilder.implements(clazz: KClass<*>) = unFinished { implements(clazz) }
infix fun InterfaceBuilder.implements(clazz: KClass<*>) = addImplement(clazz.asTypeName())

//----------------------------------------------- other functions

fun InterfaceAcceptor.interf(name: String, init: InterfaceBuilder.() -> Unit) = interfBuilder().build(name, init)
fun InterfaceAcceptor.interf(name: String) = incompleteBuilder().unFinished { startBuild(name) }

fun InterfaceAcceptor.interf(type: TypeSpec) = accept(type.let {
    if (this is IAccessor<*> && modifiers.isNotEmpty()) {
        it.buildUpon {
            addModifiers(*this@interf.modifiers)
        }
    } else it
})
