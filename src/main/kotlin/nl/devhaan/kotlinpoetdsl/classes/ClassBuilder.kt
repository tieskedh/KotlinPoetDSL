package nl.devhaan.kotlinpoetdsl.classes

import com.squareup.kotlinpoet.*
import nl.devhaan.kotlinpoetdsl.*
import nl.devhaan.kotlinpoetdsl.`interface`.InterfaceAcceptor
import nl.devhaan.kotlinpoetdsl.codeblock.CodeBlockBuildScript
import nl.devhaan.kotlinpoetdsl.constructorBuilder.*
import nl.devhaan.kotlinpoetdsl.functions.FunctionAcceptor
import nl.devhaan.kotlinpoetdsl.properties.PropAcceptor


class ClassAccessor(
        modifiers: MutableSet<KModifier>,
        private val clazz: ClassBuilder
) : Accessor<ClassAccessor>(modifiers),
        FunctionAcceptor by clazz,
        ClassAcceptor by clazz,
        PropAcceptor by clazz,
        InterfaceAcceptor by clazz,
        ProvideBuilderAcceptor by clazz,
        ConstructorAcceptor by clazz {
    override fun accept(func: FunSpec) = clazz.accept(func)
    override fun accept(type: TypeSpec) = clazz.accept(type)
    override fun registerBuilder(builder: IBuilder) = clazz.registerBuilder(builder)
    override fun unregisterBuilder(builder: IBuilder) = clazz.unregisterBuilder(builder)
}


class ClassBuilder(
        private val accessor: IAccessor<*>,
        private val adding: (TypeSpec) -> Unit
) : FunctionAcceptor,
        AccessorContainer<ClassAccessor>,
        ClassAcceptor,
        PropAcceptor,
        ProvideBuilderAcceptor,
        IBuilder,
        InterfaceAcceptor,
        ConstructorAcceptor {

    private val builders = mutableSetOf<IBuilder>()
    override fun registerBuilder(builder: IBuilder) {
        builders += builder
    }

    override fun unregisterBuilder(builder: IBuilder) {
        builders -= builder
    }

    fun build(): TypeSpec {
        builders.forEach { it.finish() }
        return builder.build().also(adding)
    }

    override fun finish() {
        build()
    }

    fun addModifiers(vararg modifier: KModifier) {
        builder.addModifiers(*modifier)
    }

    private var primaryConstructorSet = false
    override fun accept(constructorSpec: ConstructorSpec) {
        if (constructorSpec.isPrimary){
            require(!primaryConstructorSet) { "primary constructor is already set" }
            primaryConstructorSet = true
        }
        builder.addConstructor(constructorSpec)
    }

    override fun accept(func: FunSpec) {
        builder.addFunction(func)
    }

    override fun accept(type: TypeSpec) {
        builder.addType(type)
    }

    override fun accept(prop: PropertySpec) {
        builder.addProperty(prop)
    }

    override fun accessors(vararg modifier: KModifier) = ClassAccessor(modifier.toMutableSet(), this)

    private lateinit var builder: TypeSpec.Builder

    private fun initBuilder(name: String): TypeSpec.Builder {
        return TypeSpec.classBuilder(name).addModifiers(*accessor.modifiers)
                .also { builder = it }
    }

    fun initClazz(name: String, vararg variables: Variable) = apply {
        initBuilder(name)
        if (variables.isNotEmpty()) primaryConstructor(*variables)
    }

    fun buildClazz(name: String, vararg variables: Variable, init: ClassBuilder.() -> Unit = {}) = build(name) {
        if (variables.isNotEmpty()) primaryConstructor(*variables)
        init(this)
    }


    fun primaryConstructor(vararg variables: Variable, blockWrapper: CodeBlockBuildScript = {}) {
        primary.constructor(*variables, init = blockWrapper)
    }


    private inline fun build(name: String, buildScript: ClassBuilder.() -> Unit = {}): TypeSpec {
        initBuilder(name)
        buildScript(this)
        return build()
    }

    fun addImplement(typeName: TypeName, name: String) = apply {
        builder.addSuperinterface(typeName, name)
    }

    fun addImplement(typeName: TypeName, codeBlock: CodeBlock = EMPTY_CODEBLOCK) = apply {
        builder.addSuperinterface(typeName, codeBlock)
    }

    fun buildImplement(extensionData: ClassAcceptor.ExtensionData) = extensionData.run { buildImplement(typeName, codeBlock, builder) }
    fun build(buildScript: ClassBuilder.() -> Unit): TypeSpec {
        buildScript(this)
        return build()
    }

    fun buildImplement(typeName: TypeName, codeBlock: CodeBlock = EMPTY_CODEBLOCK, buildScript: ClassBuilder.() -> Unit = {}): TypeSpec {
        builder.addSuperinterface(typeName, codeBlock)
        buildScript(this)
        return build()
    }

    fun buildImplement(delVar: ClassAcceptor.DelegationProperty): TypeSpec {
        builder.addSuperinterface(delVar.typeName!!, delVar.name)
        delVar.script(this)
        return build()
    }

    fun addExtend(typeName: TypeName, codeBlock: CodeBlock = EMPTY_CODEBLOCK) = apply {
        builder.superclass(typeName)
        codeBlock.takeUnless { it.isEmpty() }?.let { builder.addSuperclassConstructorParameter(it) }
    }
    fun buildExtend(extensionData: ClassAcceptor.ExtensionData) = extensionData.run { buildExtend(typeName, codeBlock, builder) }
    fun buildExtend(typeName: TypeName, codeBlock: CodeBlock = EMPTY_CODEBLOCK, buildScript: ClassBuilder.() -> Unit = {}): TypeSpec {
        addExtend(typeName, codeBlock)
        buildScript(this)
        return build()
    }
}

fun TypeSpec.buildUpon(build: TypeSpec.Builder.() -> Unit) = toBuilder().also(build).build()