package nl.devhaan.kotlinpoetdsl


import com.squareup.kotlinpoet.*

val EMPTY_CODEBLOCK = CodeBlock.builder().build()
fun TypeSpec.packaged(pack: String, vararg outerClasses: String): ClassName {
    return if (outerClasses.isEmpty()) ClassName(pack, name!!)
    else ClassName.bestGuess((listOf(pack) + outerClasses+name!!).joinToString("."))
}
inline fun <reified T> typeNameFor(): ClassName {
    val typeName = T::class.asTypeName()
    return if (null is T) typeName.asNullable() else typeName.asNonNullable()
}
fun String.S() = "\"$this\""

fun TypeSpec.println() = println(this)
fun FunSpec.println() = println(this)
fun CodeBlock.println() = println(this)