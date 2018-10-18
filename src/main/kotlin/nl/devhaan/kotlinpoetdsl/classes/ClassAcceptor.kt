package nl.devhaan.kotlinpoetdsl.classes

import com.squareup.kotlinpoet.*
import nl.devhaan.kotlinpoetdsl.*
import nl.devhaan.kotlinpoetdsl.ProvideBuilderAcceptor.ImplementationData
import nl.devhaan.kotlinpoetdsl.inheritance.ImplementationDSL
import kotlin.reflect.KClass

interface ClassAcceptor : IAcceptor {
    fun accept(type: TypeSpec)
    data class BodylessExtensionData(val codeBlock: CodeBlock = EMPTY_CODEBLOCK, val typeName: TypeName)
    data class ExtensionData(val codeBlock: CodeBlock = EMPTY_CODEBLOCK, val typeName: TypeName, val builder: ClassBuilder.() -> Unit = {})
    class DelegationProperty(val name: String, var script: ClassBuilder.() -> Unit = {}, var typeName: TypeName? = null)

    fun delVar(name: String) = DelegationProperty(name)

    operator fun KClass<*>.invoke(format: String, vararg parts: Any?) = BodylessExtensionData(
            codeBlock = CodeBlock.of(format, *parts),
            typeName = this.asTypeName()
    )

    operator fun KClass<*>.invoke(format: String, vararg parts: Any?, builder: ClassBuilder.() -> Unit = {}) = ExtensionData(
            codeBlock = CodeBlock.of(format, *parts),
            typeName = this.asTypeName(),
            builder = builder
    )

    operator fun KClass<*>.invoke(delVar: DelegationProperty, builder: ClassBuilder.() -> Unit = {}) = delVar.also {
        it.script = builder
        it.typeName = this.asTypeName()
    }


    infix fun ClassBuilder.implements(implementDsl: ImplementationDSL.() -> Unit) = apply {
        ImplementationDSL(this).also(implementDsl)
    }

    fun ClassBuilder.implements(implementDsl: ImplementationDSL.() -> Unit, builder: ClassBuilder.() -> Unit): TypeSpec = implements(implementDsl).build(builder)


    infix fun ClassBuilder.implements(typeName: TypeName) = addImplement(typeName)
    infix fun ClassBuilder.implements(clazz: KClass<*>) = addImplement(clazz.asTypeName())

    infix fun ClassBuilder.implements(implementationData: ImplementationData<ClassBuilder>)
            = buildImplement(implementationData.typeName, buildScript = implementationData.buildScript)
    infix fun ClassBuilder.implements(extensionData: ClassAcceptor.ExtensionData): TypeSpec = buildImplement(extensionData)
    infix fun ClassBuilder.implements(extensionData: ClassAcceptor.BodylessExtensionData) = addImplement(extensionData.typeName, extensionData.codeBlock)
    infix fun ClassBuilder.implements(delVar: DelegationProperty): TypeSpec = buildImplement(delVar)

    infix fun ClassBuilder.extends(typeName: TypeName) = addExtend(typeName)
    infix fun ClassBuilder.extends(clazz: KClass<*>) = addExtend(clazz.asTypeName())
    infix fun ClassBuilder.extends(extensionData: ClassAcceptor.BodylessExtensionData) = addExtend(extensionData.typeName, extensionData.codeBlock)

    infix fun ClassBuilder.extends(implData: ImplementationData<ClassBuilder>) = buildExtend(implData.typeName, buildScript = implData.buildScript)
    infix fun ClassBuilder.extends(extensionData: ExtensionData): TypeSpec = buildExtend(extensionData)
}

fun ClassAcceptor.classBuilder() = ClassBuilder(
        accessor = this as? IAccessor<*> ?: PlainAccessor(),
        adding = ::accept
).also(this::registerBuilder)

fun ClassAcceptor.clazz(typeSpec: TypeSpec) = accept(typeSpec.let {
    if (this is IAccessor<*>) {
        it.buildUpon {
            addModifiers(*this@clazz.modifiers)
        }
    } else it
})

fun ClassAcceptor.clazz(name: String, vararg variables: Variable) =
        classBuilder().initClazz(name, *variables)

fun ClassAcceptor.clazz(name: String, vararg variables: Variable, init: ClassBuilder.() -> Unit): TypeSpec {
    return classBuilder().buildClazz(name, *variables, init = init)
}

fun ClassAcceptor.clazz(name: String, property: PropertySpec, vararg properties: PropertySpec, init: ClassBuilder.() -> Unit = {}) =
        classBuilder().buildClazz(name, property.toVariable(), *properties.map { it.toVariable() }.toTypedArray(), init = init)

fun ClassAcceptor.clazz(name: String, param: ParameterSpec, vararg params: ParameterSpec, init: ClassBuilder.() -> Unit = {}) =
        classBuilder().buildClazz(name, param.toVariable(), *params.map { it.toVariable() }.toTypedArray(), init = init)




