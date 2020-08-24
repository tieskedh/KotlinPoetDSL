package nl.devhaan.kotlinpoetdsl


import com.squareup.kotlinpoet.*

val EMPTY_CODEBLOCK = CodeBlock.builder().build()
fun TypeSpec.packaged(pack: String, vararg outerClasses: String): ClassName {
    return if (outerClasses.isEmpty()) ClassName(pack, name!!)
    else ClassName.bestGuess((listOf(pack) + outerClasses+name!!).joinToString("."))
}
inline fun <reified T> typeNameFor(): TypeName {
    val typeName = T::class.asTypeName()
    return when {
        null is T -> typeName.copy(nullable = true)
        else -> typeName.copy(nullable = false)
    }
}
fun String.S() = "\"$this\""

fun TypeSpec.println() = println(this)
fun FunSpec.println() = println(this)
fun CodeBlock.println() = println(this)