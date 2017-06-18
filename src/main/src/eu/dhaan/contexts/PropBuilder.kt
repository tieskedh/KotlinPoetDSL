package eu.dhaan.contexts

import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.PropertySpec.Builder
import eu.dhaan.SingleParameter

class PropBuilder(private val build: (PropertySpec)->Unit){
    lateinit var builder: Builder

    operator fun invoke(parameter: SingleParameter, buildScript: PropBuilder.()->Unit): PropertySpec?{
        builder = PropertySpec.builder(parameter.first, parameter.second.clazz)
        buildScript(this)
        return builder.build().apply(build)
    }

    fun init(name:String){
        builder.initializer(name)
    }
}