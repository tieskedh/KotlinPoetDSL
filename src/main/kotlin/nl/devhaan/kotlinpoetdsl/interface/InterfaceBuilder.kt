package nl.devhaan.kotlinpoetdsl.`interface`

import com.squareup.kotlinpoet.*
import nl.devhaan.kotlinpoetdsl.*
import nl.devhaan.kotlinpoetdsl.ProvideBuilderAcceptor.ImplementationData
import nl.devhaan.kotlinpoetdsl.classes.ClassAcceptor
import nl.devhaan.kotlinpoetdsl.functions.FunctionAcceptor
import nl.devhaan.kotlinpoetdsl.properties.PropAcceptor

class InterfaceAccessor(
        modifiers: MutableSet<KModifier>,
        private val interf: InterfaceBuilder
) : Accessor<InterfaceAccessor>(modifiers),
        FunctionAcceptor by interf,
        ClassAcceptor by interf,
        PropAcceptor by interf,
        InterfaceAcceptor by interf{
    override fun accept(type: TypeSpec) = interf.accept(type)
    override fun registerBuilder(builder: IBuilder) = interf.registerBuilder(builder)
}

class InterfaceBuilder(
       private val accessor: IAccessor<*> = PlainAccessor(),
       private val adding: (TypeSpec) -> Unit
) : FunctionAcceptor, AccessorContainer<InterfaceAccessor>, ClassAcceptor, PropAcceptor, IBuilder, InterfaceAcceptor{

    private lateinit var builder: TypeSpec.Builder

    override fun accept(func: FunSpec) {
        builder.addFunction(func)
    }

    override fun accept(type: TypeSpec) {
        builder.addType(type)
    }

    override fun accept(prop: PropertySpec) {
        builder.addProperty(prop)
    }

    override fun accessors(vararg modifier: KModifier) =
            InterfaceAccessor(modifier.toMutableSet(), this)

    private val builders = mutableListOf<IBuilder>()
    override fun registerBuilder(builder: IBuilder) {
        builders += builder
    }

    override fun finish(){
        builders.forEach { it.finish() }
        adding(builder.build())
    }

    private fun initBuilder(name: String): TypeSpec.Builder {
        return TypeSpec.interfaceBuilder(name).addModifiers(*accessor.modifiers).also { builder = it }
    }

    fun build(name: String, buildScript: InterfaceBuilder.()->Unit = {}): TypeSpec {
        initBuilder(name)
        buildScript(this)
        return builder.build()
    }

    fun addImplement(typeName: TypeName) = apply {
        builder.addSuperinterface(typeName)
    }

    fun startBuild(name: String) = apply {
        initBuilder(name)
    }

    fun finishBuild(buildScript: InterfaceBuilder.()->Unit): TypeSpec {
        buildScript()
        return builder.build()
    }

    fun finishBuild(implementationData: ImplementationData<InterfaceBuilder>) = implementationData.run {
        addImplement(typeName)
        buildScript(this@InterfaceBuilder)
        builder.build()
    }
}