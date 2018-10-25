package nl.devhaan.kotlinpoetdsl.codeblock

import nl.devhaan.kotlinpoetdsl.CodeBlockLevel
import nl.devhaan.kotlinpoetdsl.helpers.BlockWrapper

@CodeBlockLevel
interface LazyComponentAcceptor

class LazyComponent(val wrapper : (BlockWrapper<*, *, *>) -> Unit)

fun buildLazyComponent(builder : (BlockWrapper<*, *, *>) ->Unit) = LazyComponent(builder)
