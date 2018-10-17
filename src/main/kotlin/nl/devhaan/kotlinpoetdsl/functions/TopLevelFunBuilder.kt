package nl.devhaan.kotlinpoetdsl.functions

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import nl.devhaan.kotlinpoetdsl.Accessor
import nl.devhaan.kotlinpoetdsl.AccessorContainer
import nl.devhaan.kotlinpoetdsl.IBuilder

class TopLevelFunAccessor(
        modifier: MutableSet<KModifier>,
        private val topLevelFun: TopLevelFunBuilder
) : Accessor<TopLevelFunAccessor>(modifier), FunctionAcceptor by topLevelFun

class TopLevelFunBuilder : FunctionAcceptor, AccessorContainer<TopLevelFunAccessor> {
    override fun accessors(vararg modifier: KModifier) = TopLevelFunAccessor(modifier.toMutableSet(), this)
    private val builders = mutableListOf<IBuilder>()
    lateinit var funSpec: FunSpec

    fun build(): FunSpec {
        builders.forEach { it.finish() }
        return funSpec
    }

    override fun accept(func: FunSpec) {
        funSpec = func
    }

}

fun buildFun(builder: TopLevelFunBuilder.() -> FunSpec) =
        TopLevelFunBuilder().also { builder(it) }.build()