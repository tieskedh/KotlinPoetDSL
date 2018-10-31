package nl.devhaan.kotlinpoetdsl

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import com.sun.org.apache.bcel.internal.classfile.Code
import nl.devhaan.kotlinpoetdsl.Variable.PropertyData
import kotlin.reflect.KClass

// ----------------------------------------- of ----------------------------------------------------------------------
// ----------------- blank
infix fun <T : Any> String.of(clazz: KClass<T>) = Variable(this, clazz.asTypeName())
infix fun String.of(typeName: TypeName) = Variable(this, typeName)
// ----------------- codeblock
fun String.of(typeName: TypeName, codeBlock: CodeBlock? = null) = Variable(
        name = this,
        typeName = typeName,
        initializer = codeBlock
)
inline fun <reified T> String.of(codeBlock: CodeBlock? = null) =
        of(typeNameFor<T>(), codeBlock)
// ---------------- format
fun String.of(
        typeName: TypeName,
        format: String,
        vararg values: Any?
) = of(typeName, CodeBlock.of(format, *values))
inline fun <reified T> String.of(format: String, vararg values: Any?) =
        of(typeNameFor<T>(), CodeBlock.of(format, *values))

// ------------------------------------- valOf -----------------------------------------------------------------------
// ------------------ blank
infix fun <T : Any> String.valOf(clazz: KClass<T>) = this valOf clazz.asTypeName()
infix fun String.valOf(typeName: TypeName) = Variable(
        name = this,
        typeName = typeName,
        propertyData = PropertyData(mutable = false)
)
// ------------------ code block
inline fun <reified T : Any> String.valOf(codeBlock: CodeBlock? = null) = valOf(typeNameFor<T>(), codeBlock)
fun String.valOf(typeName: TypeName, codeBlock: CodeBlock? = null) = Variable(
        name = this,
        typeName = typeName,
        propertyData = PropertyData(mutable = false),
        initializer = codeBlock
)
// ------------------- format

inline fun <reified T : Any> String.valOf(format: String, vararg values: Any?) =
        this.valOf(typeNameFor<T>(), CodeBlock.of(format, *values))
fun String.valOf(typeName: TypeName, format: String, vararg values: Any?) =
        valOf(typeName, CodeBlock.of(format, *values))


// ------------------------------------------------- varof -------------------------------------------------------------
// ----------------------- blank
infix fun <T : Any> String.varOf(clazz: KClass<T>) = varOf(clazz.asTypeName())
infix fun String.varOf(typeName: TypeName) = Variable(
        name = this,
        typeName = typeName,
        propertyData= PropertyData(mutable = true)
)


// ------------------- codeBlock
inline fun <reified T : Any> String.varOf(codeBlock: CodeBlock? = null) = varOf(typeNameFor<T>(), codeBlock)
fun String.varOf(typeName: TypeName, codeBlock: CodeBlock? = null) = Variable(
        name = this,
        typeName = typeName,
        propertyData = PropertyData(mutable = true),
        initializer = codeBlock
)

// ------------------- format
inline fun <reified T : Any> String.varOf(format: String, vararg values: Any?) =
        varOf(typeNameFor<T>(), CodeBlock.of(format, *values))
fun String.varOf(typeName: TypeName, format: String, vararg values: Any?) =
        varOf(typeName, CodeBlock.of(format, *values))



// ----------------------------------------------- vararg -------------------------------------------------------------
// ----------------------------- blank
infix fun <T : Any> String.vararg(clazz: KClass<T>) = vararg(clazz.asTypeName())
infix fun String.vararg(typeName: TypeName) = Variable(
        name = this,
        typeName = typeName,
        modifiers = mutableSetOf(KModifier.VARARG)
)

// ----------------------------- codeBlock
inline fun <reified T : Any> String.vararg(codeBlock: CodeBlock? = null) = vararg(typeNameFor<T>(), codeBlock)
fun String.vararg(typeName: TypeName, codeBlock: CodeBlock? = null) = Variable(
        name = this,
        typeName = typeName,
        modifiers = mutableSetOf(KModifier.VARARG),
        initializer = codeBlock
)

//---------------------------- format

inline fun <reified T : Any> String.vararg(format: String? = null, vararg values: Any?) = vararg(typeNameFor<T>(), format, *values)
fun String.vararg(typeName: TypeName, format: String? = null, vararg values: Any?) = Variable(
        name = this,
        typeName = typeName,
        modifiers = mutableSetOf(KModifier.VARARG),
        initializer = format?.let { CodeBlock.of(it, *values) }
)


// ----------------------------------------------- varargVal -----------------------------------------------------------
// ----------------------------- blank
infix fun <T : Any> String.varargVal(clazz: KClass<T>) = varargVal(clazz.asTypeName())
infix fun String.varargVal(typeName: TypeName) = Variable(
        name = this,
        typeName = typeName,
        modifiers = mutableSetOf(KModifier.VARARG),
        propertyData = PropertyData(mutable = false)
)

// ----------------------------- codeBlock
inline fun <reified T : Any> String.varargVal(codeBlock: CodeBlock? = null) = varargVal(typeNameFor<T>(), codeBlock)
fun String.varargVal(typeName: TypeName, codeBlock: CodeBlock? = null) = Variable(
        name = this,
        typeName = typeName,
        modifiers = mutableSetOf(KModifier.VARARG),
        propertyData = PropertyData(mutable = false),
        initializer = codeBlock
)

//---------------------------- format

inline fun <reified T : Any> String.varargVal(format: String? = null, vararg values: Any?) = varargVal(typeNameFor<T>(), format, *values)
fun String.varargVal(typeName: TypeName, format: String? = null, vararg values: Any?) = Variable(
        name = this,
        typeName = typeName,
        modifiers = mutableSetOf(KModifier.VARARG),
        propertyData = PropertyData(mutable = false),
        initializer = format?.let { CodeBlock.of(it, *values) }
)

// ----------------------------------------------- varargVar -----------------------------------------------------------
// ----------------------------- blank
infix fun <T : Any> String.varargVar(clazz: KClass<T>) = varargVar(clazz.asTypeName())
infix fun String.varargVar(typeName: TypeName) = Variable(
        name = this,
        typeName = typeName,
        modifiers = mutableSetOf(KModifier.VARARG),
        propertyData = PropertyData(mutable = true)
)

// ----------------------------- codeBlock
inline fun <reified T : Any> String.varargVar(codeBlock: CodeBlock? = null) = varargVar(typeNameFor<T>(), codeBlock)
fun String.varargVar(typeName: TypeName, codeBlock: CodeBlock? = null) = Variable(
        name = this,
        typeName = typeName,
        modifiers = mutableSetOf(KModifier.VARARG),
        propertyData = PropertyData(mutable = true),
        initializer = codeBlock
)

//---------------------------- format

inline fun <reified T : Any> String.varargVar(format: String? = null, vararg values: Any?) = varargVar(typeNameFor<T>(), format, *values)
fun String.varargVar(typeName: TypeName, format: String? = null, vararg values: Any?) = Variable(
        name = this,
        typeName = typeName,
        modifiers = mutableSetOf(KModifier.VARARG),
        propertyData = PropertyData(mutable = true),
        initializer = format?.let { CodeBlock.of(it, *values) }
)