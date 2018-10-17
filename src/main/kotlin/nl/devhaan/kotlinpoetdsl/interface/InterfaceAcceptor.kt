package nl.devhaan.kotlinpoetdsl.`interface`

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import nl.devhaan.kotlinpoetdsl.IAcceptor
import nl.devhaan.kotlinpoetdsl.IAccessor
import nl.devhaan.kotlinpoetdsl.PlainAccessor
import nl.devhaan.kotlinpoetdsl.ProvideBuilderAcceptor.ImplementationData
import nl.devhaan.kotlinpoetdsl.classes.buildUpon
import nl.devhaan.kotlinpoetdsl.infix
import kotlin.reflect.KClass



interface InterfaceAcceptor : IAcceptor{
    fun accept(type: TypeSpec)

}

private fun InterfaceAcceptor.interfBuilder() = InterfaceBuilder(
        accessor = this as? IAccessor<*> ?: PlainAccessor(),
        adding = ::accept
).also(this::registerBuilder)

fun InterfaceAcceptor.interf(name: String, init: InterfaceBuilder.() -> Unit) = interfBuilder().build(name, init)

fun InterfaceAcceptor.interf(name: String) =
        interfBuilder().startBuild(name)


fun InterfaceAcceptor.interf(type: TypeSpec) = accept(type.let {
    if (this is IAccessor<*> && modifiers.isNotEmpty()) {
        it.buildUpon {
            addModifiers(*this@interf.modifiers)
        }
    } else it
})

fun InterfaceBuilder.implements(vararg clazzes: KClass<*>) = apply {
    clazzes.forEach { addImplement(it.asTypeName()) }
}

fun InterfaceBuilder.implements(vararg typeNames: TypeName) = apply {
    typeNames.forEach { addImplement(it) }
}

fun InterfaceBuilder.implements(vararg typeNames: TypeName, buildScript: InterfaceBuilder.()->Unit) =
        implements(*typeNames).finishBuild(buildScript)

fun InterfaceBuilder.implements(vararg clazzes: KClass<*>, buildScript: InterfaceBuilder.()->Unit) =
        implements(*clazzes).finishBuild(buildScript)

infix fun InterfaceBuilder.implements(typeName: TypeName) = addImplement(typeName)
infix fun InterfaceBuilder.implements(clazz: KClass<*>) = addImplement(clazz.asTypeName())
infix fun InterfaceBuilder.implements(implementationData: ImplementationData<InterfaceBuilder>) = finishBuild(implementationData)