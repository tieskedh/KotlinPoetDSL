package eu.dhaan.contexts

import eu.dhaan.constructs.controllFlows.IRepeat
import eu.dhaan.constructs.controllFlows.IfClassStart
import eu.dhaan.constructs.controllFlows.IiFInterface
import eu.dhaan.constructs.controllFlows.Repeat
import eu.dhaan.helpers.BlockWrapper

class CodeBlockBuilder
    internal constructor(
        private val builder: BlockWrapper<*,*>
    ): IRepeat by Repeat(builder),
        IiFInterface by IfClassStart(builder){

    fun statement(first:String, vararg parts:Any){
        builder.statement(first, *parts)
    }

    fun build() = builder.build()
}