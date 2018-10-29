package nl.devhaan.kotlinpoetdsl.codeblock

import com.squareup.kotlinpoet.CodeBlock
import nl.devhaan.kotlinpoetdsl.CodeBlockLevel
import nl.devhaan.kotlinpoetdsl.helpers.BlockWrapper
import nl.devhaan.kotlinpoetdsl.helpers.wrapper

@CodeBlockLevel
interface LazyComponentAcceptor

class LazyComponent(val wrapper: (BlockWrapper<*, *, *>) -> Unit, val singleStatement: Boolean)

fun LazyComponent.toCodeBlock() = CodeBlock.builder().wrapper().also(wrapper).build()
fun createLazyComponent(singleStatement: Boolean, builder: (BlockWrapper<*, *, *>) -> Unit) = LazyComponent(builder, singleStatement)
