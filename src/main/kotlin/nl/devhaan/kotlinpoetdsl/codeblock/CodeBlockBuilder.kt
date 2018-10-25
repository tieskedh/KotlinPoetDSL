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

    fun String.statement() = builder.statement(this)
    fun String.argStatement(vararg parts: Any) = builder.statement(this, *parts)

    fun statement(first:String, vararg parts:Any){
        builder.statement(first, *parts)
    }
}