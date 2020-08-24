package nl.devhaan.kotlinpoetdsl.codeblock.function

import nl.devhaan.kotlinpoetdsl.Variable
import nl.devhaan.kotlinpoetdsl.codeblock.CodeBlockBuildScript
import nl.devhaan.kotlinpoetdsl.codeblock.CodeBlockBuilder
import nl.devhaan.kotlinpoetdsl.helpers.FuncBlockWrapper

typealias FunctionBlockBuildScript = FunctionBlockBuilder.()->Unit

class FunctionBlockBuilder(
        override val builder: FuncBlockWrapper
) : CodeBlockBuilder(builder),
        IParameterFunBlock by ParameterFunBlock(builder) {
    fun creates(variable: Variable) : LocalVariable {
        builder.apply {
            val prop = variable.toPropertySpec()
            val accessor = if (prop.mutable) "var" else "val"
            val init = prop.initializer?.let { " = $it" }.orEmpty()
            statement(prop.run { "$accessor $name : $type$init"})
            return LocalVariable(variable)
        }
    }

    fun returns(param: LocalVariable, overwriteReturnType: Boolean = true) {
        statement("return " + param.variable.name)
        if (overwriteReturnType){
            builder.returns(param.variable.typeName)
        }
    }
}