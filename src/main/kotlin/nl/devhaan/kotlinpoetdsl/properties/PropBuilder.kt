package nl.devhaan.kotlinpoetdsl.properties

import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.PropertySpec.Builder
import nl.devhaan.kotlinpoetdsl.Variable


class PropBuilder(private val build: (PropertySpec)->Unit){
    lateinit var builder: Builder

    operator fun invoke(variable: Variable, buildScript: PropBuilder.() -> Unit) = variable.toPropertySpec().toBuilder().also {
        builder = it
        buildScript(this)
    }.build().also(build)

    fun init(name:String){
        builder.initializer(name)
    }
}