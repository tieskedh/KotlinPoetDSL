package nl.devhaan.kotlinpoetdsl.classes

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import nl.devhaan.kotlinpoetdsl.Accessor
import nl.devhaan.kotlinpoetdsl.AccessorContainer
import nl.devhaan.kotlinpoetdsl.IBuilder

class TopLevelClassAccessor(
        modifier: MutableSet<KModifier>,
        private val topLevelClass: TopLevelClassBuilder
) : Accessor<TopLevelClassAccessor>(modifier), ClassAcceptor by topLevelClass

class TopLevelClassBuilder : ClassAcceptor, AccessorContainer<TopLevelClassAccessor>{
    override fun accessors(vararg modifier: KModifier) = TopLevelClassAccessor(modifier.toMutableSet(), this)
    private val builders = mutableListOf<IBuilder>()
    private lateinit var clazz: TypeSpec

    fun build(): TypeSpec {
        builders.forEach { it.finish() }
        return clazz
    }

    override fun registerBuilder(builder: IBuilder) {
        builders += builder
    }

    override fun accept(type: TypeSpec) {
        clazz = type
    }

}

fun buildClass(builder: TopLevelClassBuilder.() -> TypeSpec) =
        TopLevelClassBuilder().also { builder(it) }.build()