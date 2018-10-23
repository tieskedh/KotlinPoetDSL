package nl.devhaan.kotlinpoetdsl.constructorBuilder

import com.squareup.kotlinpoet.KModifier
import nl.devhaan.kotlinpoetdsl.Accessor
import nl.devhaan.kotlinpoetdsl.AccessorContainer
import nl.devhaan.kotlinpoetdsl.IBuilder

class TopLevelConstructorAccessor(
        modifier: MutableSet<KModifier>,
        private val topLevelConstructor: TopLevelConstructorBuilder
) : Accessor<TopLevelConstructorAccessor>(modifier), ConstructorAcceptor by topLevelConstructor

class TopLevelConstructorBuilder : ConstructorAcceptor, AccessorContainer<TopLevelConstructorAccessor> {

    override fun accessors(vararg modifier: KModifier) =  TopLevelConstructorAccessor(modifier.toMutableSet(), this)

    private val builders = mutableSetOf<IBuilder>()
    private lateinit var constructor: ConstructorSpec


    override fun accept(constructorSpec: ConstructorSpec) {
        constructor = constructorSpec
    }

    override fun registerBuilder(builder: IBuilder) {
        builders += builder
    }

    fun build(): ConstructorSpec {
        builders.forEach { it.finish() }
        return constructor
    }

    override fun unregisterBuilder(builder: IBuilder) {
        builders -= builder
    }
}

fun buildConstructor(builder: TopLevelConstructorBuilder.() -> ConstructorSpec) =
        TopLevelConstructorBuilder().also { builder(it) }.build()