package nl.devhaan.kotlinpoetdsl.constructorBuilder

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import nl.devhaan.kotlinpoetdsl.*
import nl.devhaan.kotlinpoetdsl.codeblock.CodeBlockBuildScript
import nl.devhaan.kotlinpoetdsl.constructorBuilder.ConstructorAcceptor.IncompleteConstructorBuilder

interface ConstructorAcceptor : IAcceptor {
    class IncompleteConstructorBuilder(val acceptor: ConstructorAcceptor, val builder: ConstructorBuilder)

    fun accept(constructorSpec: ConstructorSpec)

    fun Primary.constructor(vararg variable: Variable, init: CodeBlockBuildScript = {})
            = constructorBuilder().buildPrimary(modifiers, variable, init)

    fun FunSpec.attachConstructor() = accept(toConstructor())
    fun FunSpec.attachConstructor(editModifiers: ModifierEditorDSL.() -> Set<KModifier>) = toConstructor().attachConstructor(editModifiers)

    fun ConstructorSpec.attachConstructor() = accept(this)
    fun ConstructorSpec.attachConstructor(editModifiers: ModifierEditorDSL.() -> Set<KModifier>){
        buildUpon {
            setModifiers(
                    *ModifierEditorDSL(funSpec.modifiers).editModifiers().toTypedArray()
            )
        }.attachConstructor()
    }
}

//----------------------------- only incomplete
fun IncompleteConstructorBuilder.thiz(vararg params: String) = unFinished { setThiz(*params) }

fun IncompleteConstructorBuilder.thiz(vararg params: String, script: CodeBlockBuildScript) = finish {
    setThiz(*params)
    build(script)
}

fun IncompleteConstructorBuilder.zuper(vararg params: String) = unFinished { setSuper(*params) }
fun IncompleteConstructorBuilder.zuper(vararg params: String, script: CodeBlockBuildScript) = finish {
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


fun ConstructorAcceptor.constructor(vararg variable: Variable, init: CodeBlockBuildScript) = constructorBuilder().build(variable, init)

fun ConstructorAcceptor.constructor(vararg variable: Variable) = incompleteConstructorBuilder().unFinished {
    addVariables(*variable)
}

class Primary(val modifiers: Array<out KModifier>)

inline val ConstructorAcceptor.primary get()= Primary(
        (this as? IAccessor<*>)?.modifiers.orEmpty()
)