package eu.dhaan.contexts

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.PropertySpec.Builder
import eu.dhaan.Parameter
import java.util.*

class PropBuilder(private val build: (PropertySpec)->Unit){
    lateinit var builder: Builder
    operator fun invoke(parameter: Parameter, buildScript: PropBuilder.()->Unit): PropertySpec?{
        val(name, classWrapper) = parameter
        val modifiers =classWrapper.modifiers
        if (modifiers.remove(KModifier.VARARG)){
            builder = PropertySpec.builder(name, classWrapper.clazz.getArray(),*modifiers.toTypedArray())
        }
        buildScript(this)
        return builder.build().apply(build)
    }

    inline fun <reified T> T.getArray()= Array<T?>(1){ nr->null}::class
    fun init(name:String){
        builder.initializer(name)
    }
}