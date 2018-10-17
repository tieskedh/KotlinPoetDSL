package nl.devhaan.kotlinpoetdsl.`interface`

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import nl.devhaan.kotlinpoetdsl.Accessor
import nl.devhaan.kotlinpoetdsl.AccessorContainer
import nl.devhaan.kotlinpoetdsl.IBuilder

class TopLevelInterfaceAccessor(
        modifier: MutableSet<KModifier>,
        private val topLevelInterf: TopLevelInterfaceBuilder
) : Accessor<TopLevelInterfaceAccessor>(modifier), InterfaceAcceptor by topLevelInterf

class TopLevelInterfaceBuilder : InterfaceAcceptor, AccessorContainer<TopLevelInterfaceAccessor> {
    override fun accessors(vararg modifier: KModifier) = TopLevelInterfaceAccessor(modifier.toMutableSet(), this)
    private val builders = mutableListOf<IBuilder>()
    private lateinit var interf: TypeSpec

    fun build(): TypeSpec {
        builders.forEach { it.finish() }
        return interf
    }

    override fun registerBuilder(builder: IBuilder) {
        builders += builder
    }

    override fun accept(type: TypeSpec) {
        interf = type
    }
}

fun buildInterface(builder: TopLevelInterfaceBuilder.()->TypeSpec) = TopLevelInterfaceBuilder().also { builder(it) }.build()