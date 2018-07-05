package nl.devhaan.kotlinpoetdsl.classes

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import nl.devhaan.kotlinpoetdsl.IAccessor
import nl.devhaan.kotlinpoetdsl.PlainAccessor
import nl.devhaan.kotlinpoetdsl.Variable
import nl.devhaan.kotlinpoetdsl.toVariable

interface ClassAcceptor {
    fun accept(clazz: TypeSpec)
}

fun ClassAcceptor.classBuilder() = ClassBuilder(
        accessor = this as? IAccessor<*> ?: PlainAccessor(),
        adding = ::accept
)
fun ClassAcceptor.clazz(typeSpec: TypeSpec) = accept(typeSpec.let {
    if (this is IAccessor<*>){
        it.buildUpon {
            addModifiers(*this@clazz.modifiers)
        }
    } else it
})

fun ClassAcceptor.clazz(name: String, vararg variables: Variable, init: ClassBuilder.() -> Unit = {}) = classBuilder()(name, *variables, init = init)
fun ClassAcceptor.clazz(name: String, property: PropertySpec, vararg properties: PropertySpec, init: ClassBuilder.() -> Unit = {}) =
        classBuilder()(name, property.toVariable(), *properties.map{ it.toVariable() }.toTypedArray(), init = init)
fun ClassAcceptor.clazz(name: String, param: ParameterSpec, vararg params: ParameterSpec, init: ClassBuilder.() -> Unit = {}) =
        classBuilder()(name, param.toVariable(), *params.map { it.toVariable() }.toTypedArray(), init = init)

fun buildClass(vararg modifiers: KModifier, builder: ClassAcceptor.() -> TypeSpec) = object : ClassAcceptor {
    override fun accept(clazz: TypeSpec) = Unit
}.let(builder).run{
    if (modifiers.isNotEmpty()) buildUpon { addModifiers(*modifiers) }
    else this
}