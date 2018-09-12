package nl.devhaan.kotlinpoetdsl


import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.TypeSpec

val EMPTY_CODEBLOCK = CodeBlock.builder().build()
fun TypeSpec.packaged(pack: String, vararg outerClasses: String) = ClassName.bestGuess((listOf(pack) + outerClasses).joinToString("."+name!!))