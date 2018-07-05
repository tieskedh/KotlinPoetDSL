package nl.devhaan.kotlinpoetdsl.properties

import com.squareup.kotlinpoet.PropertySpec
import nl.devhaan.kotlinpoetdsl.Variable

interface PropAcceptor{
    fun property(prop: PropertySpec)
}

fun PropAcceptor.propBuilder() = PropBuilder(
        build = ::property
)
fun PropAcceptor.prop(variable: Variable, buildScript: PropBuilder.()->Unit={}) = propBuilder()(variable, buildScript)