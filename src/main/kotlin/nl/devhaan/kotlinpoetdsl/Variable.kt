package nl.devhaan.kotlinpoetdsl

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import nl.devhaan.kotlinpoetdsl.Variable.PropertyData
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class LazySettable<T : Any>(private var build: () -> T) : ReadWriteProperty<Any, T> {
    private var field: T? = null

    override fun getValue(thisRef: Any, property: KProperty<*>) = field ?: build().also { field = it }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        field = value
    }
}


/**
 * A variable is the result of a merge from PropertySpec and ParamSpec.
 * It can be used as PropertySpec and as ParameterSpec
 */
data class Variable(
        val name: String,
        val typeName: TypeName,
        val modifiers: Set<KModifier> = setOf(),
        val initializer: CodeBlock? = null,
        val annotiations: List<AnnotationSpec> = mutableListOf(),
        val kdoc: CodeBlock = EMPTY_CODEBLOCK,
        val propertyData: PropertyData? = null
) {

    constructor(propertySpec: PropertySpec) : this(
            propertySpec.name,
            propertySpec.type,
            propertySpec.modifiers,
            propertySpec.initializer,
            propertySpec.annotations,
            propertySpec.kdoc,
            PropertyData(propertySpec)
    ) {
        _propertySpec = propertySpec
    }

    constructor(paramSpec: ParameterSpec, propertyData: PropertyData? = null) : this(
            paramSpec.name,
            paramSpec.type,
            paramSpec.modifiers,
            paramSpec.defaultValue,
            paramSpec.annotations,
            paramSpec.kdoc,
            propertyData
    ) {
        _paramSpec = paramSpec
    }

    data class PropertyData(
            val delegate: Boolean = false,
            val receiverType: TypeName? = null,
            val getter: FunSpec? = null,
            val setter: FunSpec? = null,
            val typeVariables: List<TypeVariableName> = emptyList(),
            val mutable: Boolean = false
    ) {
        constructor(propertySpec: PropertySpec) : this(
                propertySpec.delegated,
                propertySpec.receiverType,
                propertySpec.getter,
                propertySpec.setter,
                propertySpec.typeVariables,
                propertySpec.mutable
        )
    }

    private var _propertySpec by LazySettable {
        PropCreator.fromVariable(this)
    }

    fun toPropertySpec() = _propertySpec

    private var _paramSpec by LazySettable {
        ParameterSpec.builder(
                name,
                typeName,
                *(modifiers - KModifier.INLINE).toTypedArray()
        ).also { builder ->
            initializer?.let(builder::defaultValue)
            builder.addAnnotations(annotiations)
            builder.addKdoc(kdoc)
        }.build()
    }

    fun toParamSpec() = _paramSpec

    override fun toString() = when {
        propertyData == null -> toParamSpec().toString()
        KModifier.VARARG in modifiers -> "vararg " + toPropertySpec().toString().replaceFirst("kotlin.Array<out $typeName>", "$typeName")
        else -> toPropertySpec().toString()
    }.removeSuffix("\n")
}
fun PropertySpec.toVariable() = Variable(this)
fun ParameterSpec.toVariable(propertyData: PropertyData? = null) = Variable(this, propertyData)
