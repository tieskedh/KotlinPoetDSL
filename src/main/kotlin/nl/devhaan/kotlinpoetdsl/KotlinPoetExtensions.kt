package nl.devhaan.kotlinpoetdsl

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import nl.devhaan.kotlinpoetdsl.constructorBuilder.ConstructorSpec

fun FunSpec.Builder.endControlFlow(format: String = "", vararg args: Any) = when(format.isEmpty()){
    true -> endControlFlow()
    false -> addCode("⇤}$format\n", *args)
}

fun FunSpec.Builder.addParameters(vararg variable: Variable) = apply{
    addParameters(variable.map { it.toParamSpec() })
}

fun CodeBlock.Builder.endControlFlow(format: String = "", vararg args: Any?) = when(format.isEmpty()){
    true -> endControlFlow()
    false -> add("⇤}$format\n", *args)
}

/** not tested!!! */
fun TypeSpec.Builder.addProperty(vararg variables: Variable) = apply {
    addProperties(variables.map { it.toPropertySpec() })
}

fun TypeSpec.Builder.addConstructor(constructor: ConstructorSpec) = apply{
    if (constructor.isPrimary) {
        primaryConstructor(constructor.funSpec)
        addProperties(constructor.properties)
    } else addFunction(constructor.funSpec)
}
