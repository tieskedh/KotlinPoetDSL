package nl.devhaan.kotlinpoetdsl.constructorBuilder

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import nl.devhaan.kotlinpoetdsl.IAcceptor
import nl.devhaan.kotlinpoetdsl.IAccessor
import nl.devhaan.kotlinpoetdsl.PlainAccessor
import nl.devhaan.kotlinpoetdsl.Variable
import nl.devhaan.kotlinpoetdsl.codeblock.CodeBlockBuilder
import nl.devhaan.kotlinpoetdsl.constructorBuilder.ConstructorAcceptor.IncompleteConstructorBuilder

interface ConstructorAcceptor : IAcceptor {
    class IncompleteConstructorBuilder(val acceptor: ConstructorAcceptor, val builder: ConstructorBuilder)

    fun accept(constructorSpec: ConstructorSpec)

    fun Primary.constructor(vararg variable: Variable, init: CodeBlockBuilder.()->Unit = {})
            = constructorBuilder().buildPrimary(modifiers, variable, init)

}

//----------------------------- only incomplete
fun IncompleteConstructorBuilder.thiz(vararg params: String) = unFinished { setThiz(*params) }

fun IncompleteConstructorBuilder.thiz(vararg params: String, script: CodeBlockBuilder.() -> Unit) = finish {
    setThiz(*params)
    build(script)
}

fun IncompleteConstructorBuilder.zuper(vararg params: String) = unFinished { setSuper(*params) }
fun IncompleteConstructorBuilder.zuper(vararg params: String, script: CodeBlockBuilder.() -> Unit) = finish {
    setSuper(*params)
    build(script)
}

//----------------------------- other functions

//private functions are only private such that they don't popup in DSL
private fun ConstructorAcceptor.constructorBuilder() = ConstructorBuilder(
        accessor = this as? IAccessor<*> ?: PlainAccessor(),
        adding = ::accept
)

private fun ConstructorAcceptor.incompleteConstructorBuilder() =
        IncompleteConstructorBuilder(this, constructorBuilder().also(this::registerBuilder))

private inline fun IncompleteConstructorBuilder.unFinished(buildScript: ConstructorBuilder.() -> Unit) = apply { buildScript(builder) }
private inline fun IncompleteConstructorBuilder.finish(buildScript: ConstructorBuilder.() -> ConstructorSpec): ConstructorSpec {
    acceptor.unregisterBuilder(builder)
    return buildScript(builder)
}


fun ConstructorAcceptor.constructor(vararg variable: Variable, init: CodeBlockBuilder.() -> Unit) = constructorBuilder().build(variable, init)

fun ConstructorAcceptor.constructor(vararg variable: Variable) = incompleteConstructorBuilder().unFinished {
    addVariables(*variable)
}

fun ConstructorAcceptor.constructor(funSpec: FunSpec) = accept(funSpec.toConstructor())
fun ConstructorAcceptor.constructor(constructorSpec: ConstructorSpec) = accept(constructorSpec.let {
    if (this is IAccessor<*>){
        it.buildUpon { addModifiers(*this@constructor.modifiers) }
    } else it
})

class Primary(val modifiers: Array<out KModifier>)

inline val ConstructorAcceptor.primary get()= Primary(
        (this as? IAccessor<*>)?.modifiers.orEmpty()
)