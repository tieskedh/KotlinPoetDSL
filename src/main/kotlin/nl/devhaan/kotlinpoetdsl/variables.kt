package nl.devhaan.kotlinpoetdsl

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import kotlin.reflect.KClass


infix fun <T : Any> String.of(clazz: KClass<T>) = Variable(this, clazz.asTypeName())
infix fun String.of(typeName: TypeName) = Variable(this, typeName)

inline fun <reified T : Any> String.valOf(format: String? = null, vararg values: Any?) = this.valOf(T::class.asTypeName(), format, *values)

fun String.valOf(typeName: TypeName, format: String? = null, vararg values: Any?) = Variable(
        name = this,
        typeName = typeName,
        mutable = false,
        initializer = format?.let { CodeBlock.of(format, *values) }
)

infix fun <T : Any> String.valOf(clazz: KClass<T>) = this valOf clazz.asTypeName()
infix fun String.valOf(typeName: TypeName) = Variable(
        name = this,
        typeName = typeName,
        mutable = false
)

inline fun <reified T : Any> String.varOf(format: String? = null, vararg values: Any?) = varOf(T::class.asTypeName(), format, *values)
fun String.varOf(typeName: TypeName, format: String? = null, vararg values: Any?) = Variable(
        name = this,
        typeName = typeName,
        mutable = true,
        initializer = format?.let { CodeBlock.of(format, *values) }
)


infix fun <T : Any> String.varOf(clazz: KClass<T>) = varOf(clazz.asTypeName())
infix fun String.varOf(typeName: TypeName) = Variable(
        name = this,
        typeName = typeName,
        mutable = true
)


infix fun <T : Any> String.vararg(clazz: KClass<T>) = vararg(clazz.asTypeName())
infix fun String.vararg(typeName: TypeName) = Variable(
        name = this,
        typeName = typeName,
        modifier = PlainAccessor(mutableSetOf(KModifier.VARARG))
)

inline fun <reified T : Any> String.vararg(format: String? = null, vararg values: Any?) = vararg(T::class.asTypeName(), format, *values)
fun String.vararg(typeName: TypeName, format: String? = null, vararg values: Any?) = Variable(
        name = this,
        typeName = typeName,
        modifier = PlainAccessor(mutableSetOf(KModifier.VARARG)),
        initializer = format?.let { CodeBlock.of(it, *values) }
)
