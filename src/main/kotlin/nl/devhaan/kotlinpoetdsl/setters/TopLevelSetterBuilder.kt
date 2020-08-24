package nl.devhaan.kotlinpoetdsl.setters

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import nl.devhaan.kotlinpoetdsl.Accessor
import nl.devhaan.kotlinpoetdsl.AccessorContainer

class TopLevelSetterAccessor(
        modifier: MutableSet<KModifier>,
        private val topLevelSetter : TopLevelSetterBuilder
) : Accessor<TopLevelSetterAccessor>(modifier),
        SetterAcceptor by topLevelSetter

class TopLevelSetterBuilder : SetterAcceptor,
        AccessorContainer<TopLevelSetterAccessor>{
    override fun accessors(vararg modifier: KModifier)
            = TopLevelSetterAccessor(modifier.toMutableSet(), this)

    private lateinit var setter : FunSpec
    override fun acceptSetter(func: FunSpec) {
        setter  = func
    }
    fun build() : FunSpec = setter
}

fun createSetter(builder : TopLevelSetterBuilder.()->FunSpec) =
        TopLevelSetterBuilder().also { builder(it) }.build()