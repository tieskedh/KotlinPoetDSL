package nl.devhaan.kotlinpoetdsl.classes

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import nl.devhaan.kotlinpoetdsl.IAccessor
import nl.devhaan.kotlinpoetdsl.Parameter
import nl.devhaan.kotlinpoetdsl.PlainAccessor

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

fun ClassAcceptor.clazz(name: String, vararg pars: Parameter, init: ClassBuilder.() -> Unit = {}) =
        classBuilder()(name, *pars, init = init)

fun buildClass(vararg modifiers: KModifier, builder: ClassAcceptor.() -> TypeSpec) = object : ClassAcceptor {
    override fun accept(clazz: TypeSpec) = Unit
}.let(builder).run{
    if (modifiers.isNotEmpty()) buildUpon { addModifiers(*modifiers) }
    else this
}