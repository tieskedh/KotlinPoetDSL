package nl.devhaan.kotlinpoetdsl.codeblock

import nl.devhaan.kotlinpoetdsl.CodeBlockLevel
import nl.devhaan.kotlinpoetdsl.Level
import nl.devhaan.kotlinpoetdsl.helpers.BlockWrapper

@CodeBlockLevel
@Level
class CodeBlockBuilder (
        private val builder: BlockWrapper<*, *, *>
    ): IRepeat by Repeat(builder),
        IiFInterface by IfClassStart(builder),
        ISwitch by SwitchStart(builder){

    fun String.statement(vararg parts: Any) = builder.statement(this, *parts)



    fun String.addMarginedCode(vararg args: Any, marginPrefix : String= "|") = trimMargin(marginPrefix).addCode(*args)
    fun String.addTrimmedCode(vararg args: Any) = trimIndent().addCode(*args)
    fun String.addCode(vararg args: Any) = builder.addCode(this, *args)

    fun addCode(format: String, vararg parts: Any){
        builder.addCode(format, *parts)
    }
    fun statement(first:String, vararg parts:Any){
        builder.statement(first, *parts)
    }
}