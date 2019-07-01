package nl.devhaan.kotlinpoetdsl.classes

import com.squareup.kotlinpoet.*
import nl.devhaan.kotlinpoetdsl.*
import nl.devhaan.kotlinpoetdsl.ProvideBuilderAcceptor.ImplementationData
import nl.devhaan.kotlinpoetdsl.classes.ClassAcceptor.IncompleteClassBuilder
import nl.devhaan.kotlinpoetdsl.inheritance.ImplementationDSL
import kotlin.reflect.KClass

interface ClassAcceptor : IAcceptor {
    fun accept(type: TypeSpec)

    class IncompleteClassBuilder(val acceptor : ClassAcceptor, val classBuilder: ClassBuilder)

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

    /**
     * Adds Typespec keeping modifiers
     */
    fun TypeSpec.attachClazz() = this@ClassAcceptor.accept(this)


    fun TypeSpec.attachClazz(editModifiers: ModifierEditorDSL.()->Set<KModifier>) {
        val newModifiers = ModifierEditorDSL(this.modifiers).editModifiers()
        this.buildUpon {
            this@buildUpon.modifiers.clear()
            this@buildUpon.modifiers.addAll(newModifiers)
        }.attachClazz()
    }
}


//functions are private, such that they don't popup in the DSL
private inline fun IncompleteClassBuilder.unFinished(builder: ClassBuilder.() -> Unit) = apply { builder(classBuilder) }
private inline fun IncompleteClassBuilder.finished(builder: ClassBuilder.() -> TypeSpec): TypeSpec {
    acceptor.unregisterBuilder(classBuilder)
    return builder(classBuilder)
}


// -------------------------------- only incomplete
infix fun IncompleteClassBuilder.extends(implData: ImplementationData<ClassBuilder>) = finished { buildExtend(implData.typeName, buildScript = implData.buildScript) }
fun IncompleteClassBuilder.implements(implementDsl: ImplementationDSL.() -> Unit, builder: ClassBuilder.() -> Unit) = finished {
    implements(implementDsl).build(builder)
}

infix fun IncompleteClassBuilder.implements(implementationData: ImplementationData<ClassBuilder>) = finished {
    buildImplement(implementationData.typeName, buildScript = implementationData.buildScript)
}

infix fun IncompleteClassBuilder.implements(extensionData: ClassAcceptor.ExtensionData) = finished { buildImplement(extensionData) }
infix fun IncompleteClassBuilder.implements(delVar: ClassAcceptor.DelegationProperty): TypeSpec = finished { buildImplement(delVar) }
infix fun IncompleteClassBuilder.extends(extensionData: ClassAcceptor.ExtensionData): TypeSpec = finished { buildExtend(extensionData) }

infix fun IncompleteClassBuilder.withBody(builder : ClassBuilder.()->Unit) = finished { build(builder) }
// --------------------------------- incomplete and complete
infix fun IncompleteClassBuilder.extends(clazz: KClass<*>) = unFinished { addExtend(clazz.asTypeName()) }
infix fun ClassBuilder.extends(clazz: KClass<*>) = addExtend(clazz.asTypeName())


infix fun IncompleteClassBuilder.extends(extensionData: ClassAcceptor.BodylessExtensionData) = unFinished { extends(extensionData) }
infix fun ClassBuilder.extends(extensionData: ClassAcceptor.BodylessExtensionData) = addExtend(extensionData.typeName, extensionData.codeBlock)


infix fun IncompleteClassBuilder.implements(typeName: TypeName) = unFinished { addImplement(typeName) }
infix fun ClassBuilder.implements(typeName: TypeName) = addImplement(typeName)


infix fun IncompleteClassBuilder.implements(clazz: KClass<*>) = unFinished { implements(clazz.asTypeName()) }
infix fun ClassBuilder.implements(clazz: KClass<*>) = addImplement(clazz.asTypeName())


infix fun IncompleteClassBuilder.implements(extensionData: ClassAcceptor.BodylessExtensionData) = unFinished { implements(extensionData) }
infix fun ClassBuilder.implements(extensionData: ClassAcceptor.BodylessExtensionData) = addImplement(extensionData.typeName, extensionData.codeBlock)

infix fun IncompleteClassBuilder.extends(typeName: TypeName) = unFinished { addExtend(typeName) }
infix fun ClassBuilder.extends(typeName: TypeName) = addExtend(typeName)

infix fun IncompleteClassBuilder.implements(implementDsl: ImplementationDSL.() -> Unit) = unFinished { implements(implementDsl) }
infix fun ClassBuilder.implements(implementDsl: ImplementationDSL.() -> Unit) = apply {
    ImplementationDSL(this).also(implementDsl)
}

//----------------------------------- other functions

//private functions are only private such that they don't popup in DSL
private  fun ClassAcceptor.classBuilder() = ClassBuilder(
        accessor = this as? IAccessor<*> ?: PlainAccessor(),
        adding = ::accept
)
private fun ClassAcceptor.incompleteClassBuilder() =
        IncompleteClassBuilder(this, classBuilder().also(this::registerBuilder))


fun ClassAcceptor.clazz(name: String, vararg variables: Variable, init: ClassBuilder.() -> Unit)
        = classBuilder().buildClazz(name, *variables, init = init)

fun ClassAcceptor.clazz(name: String, property: PropertySpec, vararg properties: PropertySpec, init: ClassBuilder.() -> Unit = {}) =
        classBuilder().buildClazz(name, property.toVariable(), *properties.map { it.toVariable() }.toTypedArray(), init = init)

fun ClassAcceptor.clazz(name: String, param: ParameterSpec, vararg params: ParameterSpec, init: ClassBuilder.() -> Unit = {}) =
        classBuilder().buildClazz(name, param.toVariable(), *params.map { it.toVariable() }.toTypedArray(), init = init)

fun ClassAcceptor.clazz(name: String, vararg variables: Variable) = incompleteClassBuilder().unFinished { initClazz(name, *variables) }