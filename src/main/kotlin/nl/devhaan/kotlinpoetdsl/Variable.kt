package nl.devhaan.kotlinpoetdsl

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName


open class Variable(
        val name: String,
        val typeName: TypeName,
        val modifier: IAccessor<*> = PlainAccessor(),
        val mutable: Boolean? = null,
        val initializer: CodeBlock? = null
) {
    private var parameterSpec: ParameterSpec? = null
    private var propertySpec : PropertySpec? = null

    constructor(parameterSpec: ParameterSpec,
                modifier: IAccessor<*> = PlainAccessor(),
                mutable: Boolean? = null
    ) : this(parameterSpec.name, parameterSpec.type, modifier, mutable, parameterSpec.defaultValue) {
        this.parameterSpec = parameterSpec
    }

    constructor(propertySpec: PropertySpec,
                modifier: IAccessor<*> = PlainAccessor()
    ) : this(propertySpec.name, propertySpec.type, modifier, propertySpec.mutable, propertySpec.initializer) {
        this.propertySpec = propertySpec
    }


    fun toParamSpec() = parameterSpec ?: ParameterSpec.builder(name, typeName, *modifier.modifiers).also { builder ->
        initializer?.let { builder.defaultValue(it) }
    }.build().also { parameterSpec = it }

    fun toPropertySpec() = propertySpec ?: PropertySpec.builder(name, typeName, *modifier.modifiers).also { builder ->
        initializer?.let { builder.initializer(it) }
        mutable?.also {
            println(it)
            builder.mutable(it) }
    }.build().also { propertySpec = it }

    fun copy(
            name: String = this.name,
            typeName: TypeName = this.typeName,
            modifier: IAccessor<*> = this.modifier,
            mutable: Boolean? = this.mutable,
            initializer: CodeBlock? = this.initializer
    ) = Variable(name, typeName, modifier, mutable, initializer)
}

fun PropertySpec.toVariable() = Variable(this)
fun ParameterSpec.toVariable() = Variable(this)
