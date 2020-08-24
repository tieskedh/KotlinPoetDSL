package nl.devhaan.kotlinpoetdsl.constructorBuilder

import com.squareup.kotlinpoet.KModifier
import nl.devhaan.kotlinpoetdsl.IAccessor
import nl.devhaan.kotlinpoetdsl.IBuilder
import nl.devhaan.kotlinpoetdsl.Variable
import nl.devhaan.kotlinpoetdsl.codeblock.CodeBlockBuildScript
import nl.devhaan.kotlinpoetdsl.helpers.createCodeBlock

class ConstructorBuilder(
        val accessor: IAccessor<*>,
        private val adding: (ConstructorSpec)->Unit
) : IBuilder {

    private var primary : Boolean = false
    private val builder by lazy { ConstructorSpec.constructorBuilder(primary).addModifiers(*accessor.modifiers) }


    fun addVariables(vararg variables: Variable) {
        builder.addParameters(*variables)
    }

    fun build(script: CodeBlockBuildScript): ConstructorSpec {
        builder.addCode(createCodeBlock(script))
        return build()
    }

    fun build() : ConstructorSpec = builder.build().also(adding)

    fun build(variables: Array<out Variable>, init: CodeBlockBuildScript): ConstructorSpec {
        addVariables(*variables)
        return build(init)
    }

    override fun finish() {
        build()
    }


    fun setThiz(vararg params: String) {
        builder.callThisConstructor(*params)
    }

    fun setSuper(vararg params: String) {
        builder.callSuperConstructor(*params)
    }

    fun buildPrimary(modifiers: Array<out KModifier>, variable: Array<out Variable>, init: CodeBlockBuildScript): ConstructorSpec {
        primary = true
        addModiers(*modifiers)
        addVariables(*variable)
        return build(init)
    }

    fun addModiers(vararg modifiers: KModifier) = apply {
        builder.addModifiers(*modifiers)
    }
}