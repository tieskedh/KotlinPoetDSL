package nl.devhaan.kotlinpoetdsl.helpers

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeName
import kotlin.reflect.KClass

data class ParameterData(
        val clazz: TypeName,
        val modifiers: MutableList<KModifier> = mutableListOf(),
        val defaultValue : CodeBlock? = null,
        val readOnly: Int = UNDEFINED
){
    companion object {
        const val UNDEFINED = 0
        const val VAR = 1
        const val VAL = 2
    }
}