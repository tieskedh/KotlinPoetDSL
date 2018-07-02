package nl.devhaan.kotlinpoetdsl

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import nl.devhaan.kotlinpoetdsl.helpers.ParameterData
import kotlin.reflect.KClass

infix fun <T : Any> String.of(clazz: KClass<T>) = of(clazz.asTypeName())
infix fun String.of(typeName: TypeName) = this to ParameterData(typeName)

inline fun <reified T : Any> String.valOf(format: String? = null, vararg values: Any?): Parameter = this.valOf(T::class.asTypeName(), format, *values)
fun String.valOf(typeName: TypeName, format: String? = null, vararg values: Any?) : Parameter = this to ParameterData(
        clazz = typeName,
        defaultValue = format?.let { CodeBlock.of(format, *values) },
        readOnly = ParameterData.VAL
)

infix fun <T : Any> String.valOf(clazz: KClass<T>): Parameter = this valOf clazz.asTypeName()
infix fun String.valOf(typeName: TypeName) : Parameter = this to ParameterData(
        clazz = typeName,
        readOnly = ParameterData.VAL
)

inline fun <reified T : Any> String.varOf(format: String? = null, vararg values: Any?): Parameter = varOf(T::class.asTypeName(), format, *values)
fun String.varOf(typeName: TypeName, format: String? = null, vararg values: Any?): Parameter = this to ParameterData(
        clazz = typeName,
        defaultValue = format?.let { CodeBlock.of(format, *values) },
        readOnly = ParameterData.VAR
)


infix fun <T : Any> String.varOf(clazz: KClass<T>): Parameter = varOf(clazz.asTypeName())
infix fun String.varOf(typeName: TypeName): Parameter = this to ParameterData(
        clazz = typeName,
        readOnly = ParameterData.VAR
)


infix fun <T : Any> String.vararg(clazz: KClass<T>): Parameter = vararg(clazz.asTypeName())
infix fun String.vararg(typeName: TypeName): Parameter = this to ParameterData(typeName, mutableListOf(KModifier.VARARG))

inline fun <reified T : Any> String.vararg(format: String? = null, vararg values: Any?): Parameter = vararg(T::class.asTypeName(), format, *values)
fun String.vararg(typeName: TypeName, format: String? = null, vararg values: Any?): Parameter = this to ParameterData(
        clazz = typeName,
        defaultValue = format?.let { CodeBlock.of(format, values) },
        modifiers = mutableListOf(KModifier.VARARG)
)
