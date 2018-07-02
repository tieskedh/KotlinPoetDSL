package nl.devhaan.kotlinpoetdsl.properties

import com.squareup.kotlinpoet.PropertySpec
import nl.devhaan.kotlinpoetdsl.Parameter

interface PropAcceptor{
    fun property(prop: PropertySpec)
}

fun PropAcceptor.propBuilder() = PropBuilder(
        build = ::property
)
fun PropAcceptor.prop(parameter: Parameter, buildScript: PropBuilder.()->Unit={}) = propBuilder()(parameter, buildScript)