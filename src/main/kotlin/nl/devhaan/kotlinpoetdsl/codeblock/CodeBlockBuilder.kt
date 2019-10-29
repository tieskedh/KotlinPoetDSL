package nl.devhaan.kotlinpoetdsl.codeblock

import com.squareup.kotlinpoet.CodeBlock
import nl.devhaan.kotlinpoetdsl.CodeBlockLevel
import nl.devhaan.kotlinpoetdsl.Level
import nl.devhaan.kotlinpoetdsl.helpers.BlockWrapper

@CodeBlockLevel
@Level
class CodeBlockBuilder (
        val builder: BlockWrapper<*, *, *>
    ): IRepeat by RepeatStart(builder),
        IiFInterface by IfClassStart(builder),
        ISwitch by SwitchStart(builder){

    fun String.statement(vararg parts: Any) = builder.statement(this, *parts)

    fun LazyComponent.attach() = this.wrapper(builder)
    fun String.addMarginedCode(vararg args: Any, marginPrefix : String= "|") = trimMargin(marginPrefix).addCode(*args)
    fun String.addTrimmedCode(vararg args: Any) = trimIndent().addCode(*args)
    fun String.addCode(vararg args: Any) = builder.addCode(this, *args)

    fun addCode(format: String, vararg parts: Any){
        builder.addCode(format, *parts)
    }
    fun statement(first:String, vararg parts:Any){
        builder.statement(first, *parts)
    }

    fun addCode(codeBlock: CodeBlock){
        builder.addCode(codeBlock)
    }
}