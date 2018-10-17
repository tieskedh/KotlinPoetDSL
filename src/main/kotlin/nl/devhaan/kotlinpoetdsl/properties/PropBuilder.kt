package nl.devhaan.kotlinpoetdsl.properties

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.PropertySpec.Builder
import nl.devhaan.kotlinpoetdsl.*
import nl.devhaan.kotlinpoetdsl.getters.GetterAcceptor
import nl.devhaan.kotlinpoetdsl.setters.SetterAcceptor

class PropAccessor(
        modifiers: MutableSet<KModifier>,
        private val prop: PropBuilder
) : Accessor<PropAccessor>(modifiers),
        SetterAcceptor by prop,
        GetterAcceptor by prop

class PropBuilder(
        private val accessor: IAccessor<*> = PlainAccessor(),
        private val accept: (PropertySpec) -> Unit
) : AccessorContainer<PropAccessor>,
        SetterAcceptor,
        GetterAcceptor {

    lateinit var builder: Builder

    private var setter: FunSpec? = null
    private var getter: FunSpec? = null
    private var initializer : CodeBlock? = null

    override fun acceptSetter(func: FunSpec) {
        setter = func
    }

    override fun acceptGetter(func: FunSpec) {
        getter = func
    }

    override fun accessors(vararg modifier: KModifier) = PropAccessor(modifier.toMutableSet(), this)


    fun buildProp(variable: Variable, buildScript: PropBuilder.() -> Unit): PropertySpec {
        val modifiers = variable.modifier.modifiers+ accessor.modifiers
        val updatedVal = if (KModifier.INLINE !in modifiers) variable else {
            initializer = variable.initializer
            variable.copy(initializer = null)
        }

        return updatedVal.toPropertySpec().toBuilder().also {
            builder = it
            it.addModifiers(*accessor.modifiers)
            buildScript(this)
            finishBuilding(variable)
        }.build().also(accept)
    }

    private fun finishBuilding(variable: Variable) {
        if (notInlined()) {
            buildNormally()
            return
        }

        builder.modifiers -= KModifier.INLINE
        variable.throwOnBothInitializerAndAccessors()

        if (variable.mustHaveBackingField()) {
            variable.throwIfNoBackingField()
            buildNormally()
            return
        }

        require(setter == null){ "Val ${variable.name} cannot have setter." }

        getter?.let {
            require(it.isInlined()) {
                "Getter for inline property ${variable.name} must be inlined."
            }
            buildNormally()
            return
        }

        //no getters and setters are provided, so if there is an initializer, we can use it as inline get
        requireNotNull(variable.initializer) {
            "Inlined property must have getters or setters or be initialized."
        }
        builder.addInitializerAsInlineGetter(variable.initializer)
    }

    private fun Variable.throwOnBothInitializerAndAccessors() {
        if (this@PropBuilder.initializer != null && (setter ?: getter != null)) {
            val nullSetter = setter == null
            val nullgetter = getter == null
            val errorString = buildString {
                append("Inlined property $name cannot have initializer ")
                if (!nullgetter && !nullSetter) append(", getter and setter")
                else if (nullgetter) append("and getter")
                else append("and setter")
                append('.')
            }
            throw IllegalArgumentException(errorString)
        }
    }

    private fun Variable.mustHaveBackingField() = mutable ?: false

    private fun Variable.throwIfNoBackingField() {
        val validGetter = getter?.isInlined() ?: false
        val validSetter = setter?.isInlined() ?: false

        require(validGetter && validSetter) {
            buildString {
                append("No inline ")
                if (!validGetter && !validSetter) append("getter and setter")
                else if (!validGetter) append("getter")
                else append("setter")
                append(" provided for inline var-property $name.")
            }
        }
    }

    private fun buildNormally() {
        setter?.also { builder.setter(it) }
        getter?.also { builder.getter(it) }
    }

    private fun FunSpec.isInlined() = KModifier.INLINE in this.modifiers

    private fun notInlined() = KModifier.INLINE !in builder.modifiers

    private fun Builder.addInitializerAsInlineGetter(initializer: CodeBlock) {
        this.getter(
                FunSpec.getterBuilder()
                        .addModifiers(KModifier.INLINE)
                        .addStatement("return %L", initializer)
                        .build()
        )
    }

    fun init(format: String, vararg args: Any?) = init(CodeBlock.of(format, args))

    fun init(codeBlock: CodeBlock) {
        builder.initializer(codeBlock)
    }
}

fun PropertySpec.buildUpon(build: PropertySpec.Builder.() -> Unit) = toBuilder().also(build).build()