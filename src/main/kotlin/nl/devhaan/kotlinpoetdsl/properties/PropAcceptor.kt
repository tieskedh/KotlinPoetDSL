package nl.devhaan.kotlinpoetdsl.properties

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import nl.devhaan.kotlinpoetdsl.*

interface PropAcceptor{
    fun accept(prop: PropertySpec)
    fun PropertySpec.attachProp() = accept(this)
    fun PropertySpec.attachProp(editorModifiers: ModifierEditorDSL.()->Set<KModifier>){
        val newModifiers = ModifierEditorDSL(modifiers).editorModifiers()
        buildUpon {
            modifiers.clear()
            modifiers.addAll(newModifiers)
        }.attachProp()
    }
}

fun PropAcceptor.propBuilder() = PropBuilder(
        accessor = this as? IAccessor<*> ?: PlainAccessor(),
        accept = ::accept
)
fun PropAcceptor.prop(variable: Variable, buildScript: PropBuilder.()->Unit={}) = propBuilder().buildProp(variable, buildScript)