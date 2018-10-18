package nl.devhaan.kotlinpoetdsl

@DslMarker
annotation class Level

@DslMarker
@Level
annotation class CodeBlockLevel
