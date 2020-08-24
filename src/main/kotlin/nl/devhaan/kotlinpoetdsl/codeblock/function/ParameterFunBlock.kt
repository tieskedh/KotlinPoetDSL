package nl.devhaan.kotlinpoetdsl.codeblock.function

import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import nl.devhaan.kotlinpoetdsl.Variable
import nl.devhaan.kotlinpoetdsl.helpers.FuncBlockWrapper
import nl.devhaan.kotlinpoetdsl.of
import nl.devhaan.kotlinpoetdsl.parameters.buildUpon
import nl.devhaan.kotlinpoetdsl.toVariable
import nl.devhaan.kotlinpoetdsl.typeNameFor
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

interface IParameterFunBlock {
    val params: ParamContainer
}

class ParameterFunBlock(
        funBlock: FuncBlockWrapper
) : IParameterFunBlock {
    override val params = ParamContainer(funBlock)
}

class ParamContainer(
        val builder: FuncBlockWrapper
) {
    fun create(variable: Variable): LocalVariable {
        val alreadyExist = builder.params.any { it.name == variable.name }
        if (alreadyExist) throw ParameterAlreadyExistsException(variable.name)
        builder.addParameters(arrayOf(variable))
        return LocalVariable(variable)
    }
    inline fun <reified T> creating() = creating(typeNameFor<T>())
    inline fun <reified T> creating(
            noinline paramSpec: ParameterSpec.Builder.()->Unit
    ) = creating(typeNameFor<T>(), paramSpec)

    fun creating(typeName :  TypeName)  =PropertyDelegateProvider { _: Any?, prop ->
        val alreadyExists = builder.params.any { it.name == prop.name }
        if (alreadyExists) throw ParameterAlreadyExistsException(prop.name)
        val variable = prop.name of typeName
        builder.addParameters(
                arrayOf(variable)
        )
        ReadOnlyProperty { _: Any?, _ ->
            LocalVariable(variable)
        }
    }
    fun creating(typeName :  TypeName, paramSpec: ParameterSpec.Builder.()->Unit)  =PropertyDelegateProvider { _: Any?, prop ->
        val alreadyExists = builder.params.any { it.name == prop.name }
        if (alreadyExists) throw ParameterAlreadyExistsException(prop.name)
        val variable = (prop.name of typeName)
                .toParamSpec()
                .buildUpon(paramSpec)
                .toVariable()

        builder.addParameters(
                arrayOf(variable)
        )
        ReadOnlyProperty { _: Any?, _ ->
            LocalVariable(variable)
        }
    }

    val existing = PropertyDelegateProvider { _: Any?, property ->
        val param = builder.params.find { it.name == property.name }
                ?: throw NoSuchElementException("At this moment, the function has no parameter named ${property.name}")

        ReadOnlyProperty<Any?, LocalVariable> { _, _ ->
            LocalVariable(property.name of param.type)
        }
    }
}

class LocalVariable(val variable: Variable){
    val name = variable.name
}


class ParameterAlreadyExistsException(
        val parameterName : String
) : Exception("parameter with name '$parameterName' already exists")