package nl.devhaan.kotlinpoetdsl.properties

import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.PropertySpec.Builder
import com.squareup.kotlinpoet.PropertySpec.Companion.builder
import nl.devhaan.kotlinpoetdsl.Parameter
import nl.devhaan.kotlinpoetdsl.helpers.ParameterData


class PropBuilder(private val build: (PropertySpec)->Unit){
    lateinit var builder: Builder

    operator fun invoke(parameter: Parameter, buildScript: PropBuilder.()->Unit): PropertySpec{
        val(name, classWrapper) = parameter
        val modifiers =classWrapper.modifiers
        builder = builder(name, classWrapper.clazz,*modifiers.toTypedArray())
        when(classWrapper.readOnly){
            ParameterData.VAL -> builder.mutable(false)
            ParameterData.VAR -> builder.mutable(true)
        }
        classWrapper.defaultValue?.also { builder.initializer(it)}

        buildScript(this)
        return builder.build().apply(build)
    }

    fun init(name:String){
        builder.initializer(name)
    }
}