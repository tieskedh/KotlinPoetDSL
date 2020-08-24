package nl.devhaan.kotlinpoetdsl.getters

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import nl.devhaan.kotlinpoetdsl.Accessor
import nl.devhaan.kotlinpoetdsl.AccessorContainer

class TopLevelGetterAccessor(
        modifier: MutableSet<KModifier>,
        private val topLevelGetter : TopLevelGetterBuilder
) : Accessor<TopLevelGetterAccessor>(modifier),
        GetterAcceptor by topLevelGetter

class TopLevelGetterBuilder : GetterAcceptor,
        AccessorContainer<TopLevelGetterAccessor>{
    override fun accessors(vararg modifier: KModifier)
            = TopLevelGetterAccessor(modifier.toMutableSet(), this)

    private lateinit var getter : FunSpec
    override fun acceptGetter(func: FunSpec) {
        getter  = func
    }
    fun build() : FunSpec = getter
}

fun createGetter(builder : TopLevelGetterBuilder.()->FunSpec) =
        TopLevelGetterBuilder().also { builder(it) }.build()