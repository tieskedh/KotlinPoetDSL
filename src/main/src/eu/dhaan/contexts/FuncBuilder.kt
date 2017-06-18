package eu.dhaan.contexts

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import eu.dhaan.Parameter
import eu.dhaan.constructs.Accessor
import eu.dhaan.constructs.IAccessor
import eu.dhaan.helpers.FuncBlockWrapper
import eu.dhaan.helpers.VarArg


class FuncBuilder(
        private val accessor: IAccessor = Accessor(),
        private val callBack: (FunSpec)->Unit){
    private lateinit var builder: FuncBlockWrapper
    private val codeBlockBuilder get()= CodeBlockBuilder(builder)

    operator fun invoke(name: String, buildScript: CodeBlockBuilder.()->Unit)= build(name, buildScript)

    operator fun invoke(name: String, vararg params: Parameter, buildScript: CodeBlockBuilder.()->Unit) = build(name, buildScript){
        builder.apply {
            params.forEach { (name,type)->if (type is VarArg<*>){
                addParameter(name, type.clazz, KModifier.VARARG)
            } else {
                addParameter(name, type.clazz)
            }}
        }
    }

    private fun build(name: String, codeBlockBuildScript: CodeBlockBuilder.()->Unit, buildScript: FuncBuilder.()->Unit = {}): FunSpec?{
        builder = FuncBlockWrapper(FunSpec.builder(name).addModifiers(*accessor.list))

        codeBlockBuilder.let{
            codeBlockBuildScript(it)
            buildScript(this)
            return@let it.build()
        }
        return builder.build().also(callBack)
    }
}