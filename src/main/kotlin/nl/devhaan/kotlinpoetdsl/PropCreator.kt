package nl.devhaan.kotlinpoetdsl

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec

private inline class NonInlineVariable(val variable: Variable)

object PropCreator {
    fun fromVariable(
            variable: Variable,
            propScript: PropertySpec.Builder.() -> Unit = {}
    ): PropertySpec {
        val builderSetUp = internalSetUp(variable, propScript)
        val isInline = KModifier.INLINE in builderSetUp.modifiers
        return when {
            !isInline -> builderSetUp.build()
            else -> builderSetUp
                    .toNonInlineVariable()
                    .toInlineVariable()
                    .build()
        }
    }

    private fun PropertySpec.Builder.toNonInlineVariable() = NonInlineVariable(run {
        modifiers.remove(KModifier.INLINE)
        build()
    }.toVariable())

    private fun Variable.setUpBuilder() = PropertySpec.builder(
            name,
            typeName,
            modifiers - arrayOf(KModifier.VARARG)
    ).apply {
        addAnnotations(annotiations)
        addKdoc(kdoc)
        propertyData?.let { data ->
            data.receiverType?.let(::receiver)
            data.getter?.let(::getter)
            data.setter?.let(::setter)
            addTypeVariables(data.typeVariables)
            mutable(data.mutable)
        }
    }

    private fun internalSetUp(
            variable: Variable,
            propScript: PropertySpec.Builder.() -> Unit
    ) = variable.setUpBuilder().apply {
        val isDelegated = variable.propertyData?.delegate ?: false
        variable.initializer?.let { if (isDelegated) delegate(it) else initializer(it) }
        propScript()
    }

    private fun NonInlineVariable.toInlineVariable() = variable.setUpBuilder().apply {
        //get+set and init
        variable.throwOnBothInitializerAndAccessors()

        //mutable
        if (variable.mustHaveBackingField()) {
            variable.throwIfNoBackingField()
            return@apply
        }

        //immutable
        variable.requireNoSetter()

        if (variable.hasGetter()) {
            variable.requireGetterIsInlined()
            return@apply
        }

        //otherwise use initializer
        variable.requireHasInitializer().let { init ->
            addInitializerAsInlineGetter(init)
        }
    }


    private fun Variable.requireGetterIsInlined() = require(hasInlinedGetter()) {
        "Getter for inline property '$name' must be inlined."
    }

    private fun Variable.hasInlinedGetter() = propertyData?.getter?.isInlined() == true
    private fun Variable.hasGetter() = propertyData?.getter != null

    private fun Variable.requireNoSetter() = require(propertyData?.setter == null) {
        "Val $name cannot have setter."
    }

    private fun Variable.requireHasInitializer() = requireNotNull(initializer) {
        "Inlined property must have getters or setters or be initialized."
    }


    private fun Variable.throwOnBothInitializerAndAccessors() {
        val hasSetter = propertyData?.setter != null
        val hasGetter = propertyData?.getter != null

        if (initializer != null && (hasSetter || hasGetter)) {
            val errorString = buildString {
                append("Inlined property $name cannot have initializer ")
                when {
                    hasSetter && hasGetter -> append(", getter and setter")
                    hasGetter -> append("and getter")
                    else -> append("and setter")
                }
                append('.')
            }
            throw IllegalArgumentException(errorString)
        }
    }

    private fun Variable.mustHaveBackingField() = propertyData?.mutable == true

    private fun Variable.throwIfNoBackingField() {
        val validGetter = propertyData?.getter?.isInlined() == true
        val validSetter = propertyData?.setter?.isInlined() == true

        require(validGetter && validSetter) {
            buildString {
                append("No inline ")
                when {
                    !validGetter && !validSetter -> append("getter and setter")
                    !validGetter -> append("getter")
                    else -> append("setter")
                }
                append(" provided for inline var-property $name.")
            }
        }
    }

    private fun FunSpec.isInlined() = KModifier.INLINE in this.modifiers

    private fun PropertySpec.Builder.addInitializerAsInlineGetter(
            initializer: CodeBlock
    ) {
        this.getter(
                FunSpec.getterBuilder()
                        .addModifiers(KModifier.INLINE)
                        .addStatement("return %L", initializer)
                        .build()
        )
    }
}