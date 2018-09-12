package nl.devhaan.kotlinpoetdsl.properties

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.PropertySpec.Builder
import nl.devhaan.kotlinpoetdsl.Accessor
import nl.devhaan.kotlinpoetdsl.AccessorContainer
import nl.devhaan.kotlinpoetdsl.Variable
import nl.devhaan.kotlinpoetdsl.getters.GetterAcceptor
import nl.devhaan.kotlinpoetdsl.setters.SetterAcceptor

class PropAccessor(
        modifiers: MutableSet<KModifier>,
        private val prop: PropBuilder
) : Accessor<PropAccessor>(modifiers),
        SetterAcceptor by prop,
        GetterAcceptor by prop

class PropBuilder(private val build: (PropertySpec)->Unit) : AccessorContainer<PropAccessor> ,
        SetterAcceptor,
        GetterAcceptor
{

    lateinit var builder: Builder

    override fun acceptSetter(func: FunSpec) {
        builder.setter(func)
    }
    override fun acceptGetter(func: FunSpec) {
        builder.getter(func)
    }

    override fun accessors(vararg modifier: KModifier) = PropAccessor(modifier.toMutableSet(), this)


    operator fun invoke(variable: Variable, buildScript: PropBuilder.() -> Unit) = variable.toPropertySpec().toBuilder().also {
        builder = it
        buildScript(this)
    }.build().also(build)

    fun init(format: String, vararg args: Any?) = init(CodeBlock.of(format, args))

    fun init(codeBlock: CodeBlock){
        builder.initializer(codeBlock)
    }
}

fun PropertySpec.buildUpon(build: PropertySpec.Builder.()->Unit) = toBuilder().also(build).build()