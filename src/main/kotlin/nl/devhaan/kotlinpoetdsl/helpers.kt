package nl.devhaan.kotlinpoetdsl


import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName

val EMPTY_CODEBLOCK = CodeBlock.builder().build()
fun TypeSpec.packaged(pack: String, vararg outerClasses: String) = ClassName.bestGuess((listOf(pack) + outerClasses).joinToString("."+name!!))

inline fun <reified T> typeNameFor(): ClassName {
    val typeName = T::class.asTypeName()
    return if (null is T) typeName.asNullable() else typeName.asNonNullable()
}