package nl.devhaan.kotlinpoetdsl.properties

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import nl.devhaan.kotlinpoetdsl.Accessor
import nl.devhaan.kotlinpoetdsl.AccessorContainer

class TopLevelPropAccessor(
        modifier: MutableSet<KModifier>,
        private val topLevelProp : TopLevelPropBuilder
) : Accessor<TopLevelPropAccessor>(modifier),
        PropAcceptor by topLevelProp

class TopLevelPropBuilder : PropAcceptor, AccessorContainer<TopLevelPropAccessor> {
    lateinit var propertySpec: PropertySpec

    override fun accept(prop: PropertySpec) {
        propertySpec = prop
    }

    override fun accessors(vararg modifier: KModifier) = TopLevelPropAccessor(modifier.toMutableSet(), this)

    fun build() = propertySpec
}

fun buildProp(builder: TopLevelPropBuilder.()->PropertySpec) = TopLevelPropBuilder().also { builder(it) }.build()