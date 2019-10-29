package nl.devhaan.kotlinpoetdsl.functions

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeName
import nl.devhaan.kotlinpoetdsl.*
import nl.devhaan.kotlinpoetdsl.codeblock.CodeBlockBuilder
import nl.devhaan.kotlinpoetdsl.codeblock.LazyComponent
import nl.devhaan.kotlinpoetdsl.helpers.FuncBlockWrapper

class FuncBuilder(
        private val accessor: IAccessor<*> = PlainAccessor(),
        private val callBack: (FunSpec) -> Unit
) : ProvideBuilderAcceptor, IBuilder {


    override fun finish() {
        build()
    }

    private fun build(): FunSpec = builder.build().also(callBack)

    private lateinit var builder: FuncBlockWrapper

    fun initBuilder(name: String) = FuncBlockWrapper(FunSpec.builder(name).addModifiers(*accessor.modifiers)).also {
        builder = it
    }

    fun initFunc(name: String, vararg variables: Variable) = apply {
        initBuilder(name).addParameters(variables)
    }

    fun buildReturn(typeName: TypeName, buildScript: CodeBlockBuilder.() -> Unit) =
            builder.returns(typeName).addCode(buildScript).build().also(callBack)

    fun build(lazyComponent: LazyComponent) = builder.addCode{lazyComponent.wrapper(this.builder)}.build().also(callBack)
    fun buildReturn(name: String, vararg variables: Variable, buildScript: CodeBlockBuilder.() -> Unit) = build(name, buildScript) {
        addParameters(variables)
    }

    private fun build(name: String, ReceivedBuildScript: CodeBlockBuilder.() -> Unit, internalBuildScript: FuncBlockWrapper.() -> Unit = {}) =
            initBuilder(name)
                    .also(internalBuildScript)
                    .addCode(ReceivedBuildScript)
                    .build()
                    .also(callBack)

    fun startExtensionFunction(
            receiver: TypeName,
            name: String,
            variables: Array<out Variable> = emptyArray(),
            modifiers: Array<out KModifier> = emptyArray()
    ) = apply {
        initBuilder(name).also {
            it.receiver(receiver)
            it.addParameters(variables)
            it.addModifiers(modifiers)
        }
    }

    fun buildExtensionFunction(
            receiver: TypeName,
            name: String,
            variables: Array<out Variable> = emptyArray(),
            modifiers: Array<out KModifier> = emptyArray(),
            buildScript: CodeBlockBuilder.() -> Unit
    ) = build(name, buildScript) {
        addParameters(variables)
        receiver(receiver)
        addModifiers(modifiers)
    }
}

fun FunSpec.buildUpon(build: FunSpec.Builder.() -> Unit) = toBuilder().also(build).build()