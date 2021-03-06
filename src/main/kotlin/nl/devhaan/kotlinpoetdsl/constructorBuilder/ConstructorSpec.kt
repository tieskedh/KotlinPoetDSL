package nl.devhaan.kotlinpoetdsl.constructorBuilder

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import nl.devhaan.kotlinpoetdsl.Variable

/**
 * Todo remove when KotlinPoet has ConstructorSpec
 */
class ConstructorSpec private constructor(
        val funSpec: FunSpec,
        val isPrimary: Boolean,
        private val allProperties: List<PropertySpec>,
        val activeProperties: List<PropertySpec>
) {

    init {
        if (isPrimary) require(funSpec.delegateConstructor == null) {
            "primary constructor cannot call ${funSpec.delegateConstructor}"
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ConstructorSpec) return false
        return other.isPrimary == isPrimary &&
                other.funSpec == funSpec &&
                other.allProperties == allProperties
    }

    override fun hashCode(): Int {
        var hash = 7
        hash = 31 * hash + if (isPrimary) 1 else 0
        hash = 31 * hash + funSpec.hashCode()
        hash = 31 * hash + allProperties.hashCode()
        return hash
    }

    override fun toString() = "ConstructorSpec($funSpec with properties: $activeProperties)"

    companion object {
        fun primaryConstructorBuilder() = Builder(true)
        fun constructorBuilder(primary: Boolean = false) = Builder(primary)
    }

    class Builder(
            val isPrimary: Boolean,
            private val funSpec: FunSpec.Builder = FunSpec.constructorBuilder(),
            val properties: MutableList<PropertySpec> = mutableListOf()
    ) {
        init {
            if (isPrimary) require(funSpec.build().delegateConstructor == null) {
                "primary constructor cannot call another constructor"
            }
        }

        private inline fun withFun(script: FunSpec.Builder.() -> Unit) = apply { funSpec.script() }
        fun addParameter(variable: Variable) = apply {
            funSpec.addParameter(variable.toParamSpec())
            if (variable.propertyData != null) properties += variable.copy(initializer = CodeBlock.of(variable.name)).toPropertySpec()
        }

        fun addParameters(vararg variable: Variable) = apply {
            variable.forEach { addParameter(it) }
        }

        fun addStatement(format: String, vararg parts: Any) = withFun { addStatement(format, *parts) }
        fun addCode(codeBlock: CodeBlock) = withFun { addCode(codeBlock) }
        fun callThisConstructor(vararg args: String) = withFun {
            require(!isPrimary) {
                "primary constructor cannot call this"
            }
            callThisConstructor(*args)
        }

        fun callSuperConstructor(vararg args: String) = withFun {
            require(!isPrimary) {
                "primary constructor cannot call super"
            }
            callSuperConstructor(*args)
        }

        fun build() = ConstructorSpec(
                funSpec.build(),
                isPrimary, properties,
                properties.takeIf { isPrimary }.orEmpty()
        )

        fun setModifiers(vararg modifiers: KModifier) = withFun {
            this@withFun.modifiers.let {
                it.clear()
                it.addAll(modifiers)
            }
        }
        fun addModifiers(vararg modifiers: KModifier) = withFun { addModifiers(*modifiers) }
    }

    fun toSecondary() = Builder(
            false,
            funSpec.toBuilder(),
            allProperties.toMutableList()
    ).build()

    fun toPrimary(): ConstructorSpec {
        require(funSpec.delegateConstructor == null) {
            "The constructor can't delegate if it is the primary constructor"
        }
        return Builder(
                true,
                funSpec.toBuilder(),
                allProperties.toMutableList()
        ).build()
    }

    fun toBuilder() = Builder(
            isPrimary,
            funSpec.toBuilder(),
            allProperties.toMutableList()
    )
}

/** Warning: Only constructors are allowed */
fun FunSpec.toConstructor() =
        ConstructorSpec.Builder(false, this.toFunctionConstructorBuilder()).build()

/** Warning: Only constructors are allowed */
fun FunSpec.toPrimaryConstructor(vararg properties: PropertySpec) =
        ConstructorSpec.Builder(true, this.toFunctionConstructorBuilder(), properties.toMutableList()).build()

fun FunSpec.toPrimaryConstructor(vararg variables: Variable) = ConstructorSpec.Builder(
        true,
        this.toFunctionConstructorBuilder(),
        variables.map { it.toPropertySpec() }.toMutableList()
)

private fun FunSpec.toFunctionConstructorBuilder(): FunSpec.Builder {
    require(this.isConstructor) { "Only constructors can be converted to ConstructorSpec" }
    return toBuilder()
}


fun ConstructorSpec.buildUpon(script: ConstructorSpec.Builder.() -> Unit) =
        toBuilder().also(script).build()