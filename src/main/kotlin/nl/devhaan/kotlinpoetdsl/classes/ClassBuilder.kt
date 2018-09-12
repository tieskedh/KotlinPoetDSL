package nl.devhaan.kotlinpoetdsl.classes

import com.squareup.kotlinpoet.*
import nl.devhaan.kotlinpoetdsl.*
import nl.devhaan.kotlinpoetdsl.functions.FunctionAcceptor
import nl.devhaan.kotlinpoetdsl.properties.PropAcceptor
import nl.devhaan.kotlinpoetdsl.properties.prop


class ClassAccessor(
        modifiers: MutableSet<KModifier>,
        private val clazz: ClassBuilder
) : Accessor<ClassAccessor>(modifiers),
        FunctionAcceptor by clazz,
        ClassAcceptor by clazz,
        PropAcceptor by clazz

class ClassBuilder(
        private val accessor: IAccessor<*> = PlainAccessor(),
        private val adding: (TypeSpec) -> Unit
) : FunctionAcceptor, AccessorContainer<ClassAccessor>, ClassAcceptor, PropAcceptor, ProvideBuilderAcceptor, IBuilder{

    private val builders = mutableListOf<IBuilder>()
    override fun registerBuilder(builder: IBuilder) {
        builders += builder
    }

    override fun finish() {
        builders.forEach { it.finish() }
        adding(builder.build())
    }

    fun addModifiers(vararg modifier: KModifier) {
        builder.addModifiers(*modifier)
    }

    override fun accept(func: FunSpec) {
        builder.addFunction(func)
    }

    override fun accept(clazz: TypeSpec) {
        builder.addType(clazz)
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

    operator fun invoke(name: String, vararg variables: Variable) = apply {
        initBuilder(name)
        if (variables.isNotEmpty()) primaryConstructor(variables)
    }

    operator fun invoke(name: String, vararg variables: Variable, init: ClassBuilder.() -> Unit = {}) = build(name) {
        if (variables.isNotEmpty()) primaryConstructor(variables)
        init(this)
    }

    fun primaryConstructor(variables: Array<out Variable>) {
        val primBuilder = FunSpec.constructorBuilder()
        variables.forEach { variable ->
            variable.mutable?.also { prop(variable.copy(initializer = CodeBlock.of(variable.name))) }
            primBuilder.addParameter(variable.toParamSpec())
        }
        builder.primaryConstructor(primBuilder.build())
    }


    private inline fun build(name: String, buildScript: ClassBuilder.() -> Unit = {}): TypeSpec {
        initBuilder(name)
        buildScript(this)
        return builder.build()
    }

    fun addImplement(typeName: TypeName, name: String) = apply {
        builder.addSuperinterface(typeName, name)
    }
    fun addImplement(typeName: TypeName, codeBlock: CodeBlock = EMPTY_CODEBLOCK) = apply{
        builder.addSuperinterface(typeName, codeBlock)
    }

    fun buildImplement(extensionData: ClassAcceptor.ExtensionData) = extensionData.run{ buildImplement(typeName, codeBlock, builder) }
    fun build(buildScript: ClassBuilder.() -> Unit): TypeSpec {
        buildScript(this)
        return builder.build()
    }

    fun buildImplement(typeName: TypeName, codeBlock: CodeBlock = EMPTY_CODEBLOCK, buildScript: ClassBuilder.() -> Unit = {}): TypeSpec {
        builder.addSuperinterface(typeName,codeBlock)
        buildScript(this)
        return builder.build()
    }

    fun buildImplement(delVar: ClassAcceptor.DelegationProperty): TypeSpec {
        builder.addSuperinterface(delVar.typeName!!, delVar.name)
        delVar.script(this)
        return builder.build()
    }

    fun addExtend(typeName: TypeName, codeBlock: CodeBlock = EMPTY_CODEBLOCK) = apply {
        builder.superclass(typeName)
        codeBlock.takeUnless { it.isEmpty() }?.let { builder.addSuperclassConstructorParameter(it) }
    }
    fun buildExtend(extensionData: ClassAcceptor.ExtensionData) = extensionData.run { buildExtend(typeName, codeBlock, builder) }
    fun buildExtend(typeName: TypeName, codeBlock: CodeBlock = EMPTY_CODEBLOCK, buildScript: ClassBuilder.() -> Unit = {}): TypeSpec {
        addExtend(typeName, codeBlock)
        buildScript(this)
        return builder.build()
    }
}

fun TypeSpec.buildUpon(build: TypeSpec.Builder.() -> Unit) = toBuilder().also(build).build()