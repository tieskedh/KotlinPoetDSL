package nl.devhaan.kotlinpoetdsl.codeblock

import nl.devhaan.kotlinpoetdsl.helpers.BlockWrapper

@UseExperimental(Uncertain::class)
class CodeBlockBuilder
    internal constructor(
        private val builder: BlockWrapper<*,*>
    ): IRepeat by Repeat(builder),
        IiFInterface by IfClassStart(builder) {

    fun statement(first:String, vararg parts:Any){
        builder.statement(first, *parts)
    }

    fun build() = let {
        builder.build()
    }
}