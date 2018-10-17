package nl.devhaan.kotlinpoetdsl.properties

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import nl.devhaan.kotlinpoetdsl.IAccessor
import nl.devhaan.kotlinpoetdsl.PlainAccessor
import nl.devhaan.kotlinpoetdsl.Variable

interface PropAcceptor{
    fun accept(prop: PropertySpec)
}

fun PropAcceptor.propBuilder() = PropBuilder(
        accessor = this as? IAccessor<*> ?: PlainAccessor(),
        accept = ::accept
)
fun PropAcceptor.prop(variable: Variable, buildScript: PropBuilder.()->Unit={}) = propBuilder().buildProp(variable, buildScript)
fun PropAcceptor.prop(propSpec: PropertySpec) = accept(propSpec.let {
    if (this is IAccessor<*>){
        it.buildUpon {
            addModifiers(*this@prop.modifiers)
        }
    } else it
})


fun buildProp(vararg modifiers: KModifier, builder: PropAcceptor.() -> PropertySpec) = object : PropAcceptor {
    override fun accept(prop: PropertySpec) = Unit
}.let(builder).run{
    if (modifiers.isNotEmpty()) buildUpon { addModifiers(*modifiers) }
    else this
}