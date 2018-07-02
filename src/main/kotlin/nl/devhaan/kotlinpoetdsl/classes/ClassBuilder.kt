package nl.devhaan.kotlinpoetdsl.classes


import com.squareup.kotlinpoet.*
import nl.devhaan.kotlinpoetdsl.*
import nl.devhaan.kotlinpoetdsl.functions.FunctionAcceptor
import nl.devhaan.kotlinpoetdsl.helpers.ParameterData
import nl.devhaan.kotlinpoetdsl.properties.PropAcceptor
import nl.devhaan.kotlinpoetdsl.properties.prop


class ClassAccessor(
        modifiers: MutableSet<KModifier>,
        private val clazz: ClassBuilder
) : Accessor<ClassAccessor>(modifiers),
        FunctionAcceptor by clazz,
        ClassAcceptor by clazz,
        PropAcceptor by clazz

class ClassBuilder(
        private val accessor: IAccessor<*> = PlainAccessor(),
        private val adding: (TypeSpec) -> Unit
) : FunctionAcceptor, AccessorContainer<ClassAccessor>, ClassAcceptor, PropAcceptor {

    override fun accept(func: FunSpec) {
        builder.addFunction(func)
    }

    override fun accept(clazz: TypeSpec) {
        builder.addType(clazz)
    }

    override fun property(prop: PropertySpec) {
        builder.addProperty(prop)
    }

    override fun accessors(vararg modifier: KModifier) = ClassAccessor(modifier.toMutableSet(), this)

    private lateinit var builder: TypeSpec.Builder


    fun primaryConstructor(vararg pair: Parameter) {
        val primBuilder = FunSpec.constructorBuilder()

        pair.forEach { (name, type) ->
            val modifiers = type.modifiers
            if (type.readOnly != ParameterData.UNDEFINED) {
                prop(name to type.copy(defaultValue = CodeBlock.of(name)))
            }
            primBuilder.addParameter(ParameterSpec.builder(name, type.clazz, *modifiers.toTypedArray()).also {
                type.defaultValue?.apply { it.defaultValue(this) }
            }.build())
            builder.primaryConstructor(primBuilder.build())
        }
    }

    operator fun invoke(name: String) = build(name)

    operator fun invoke(name: String, vararg pars: Parameter, init: ClassBuilder.() -> Unit = {}) = build(name) {
        primaryConstructor(*pars)
        init(this)
    }

    private inline fun build(name: String, buildScript: ClassBuilder.() -> Unit = {}): TypeSpec {
        builder = TypeSpec.classBuilder(name).also { it.addModifiers(*accessor.modifiers) }
        buildScript(this)
        return builder.build().also(adding)
    }
}

fun TypeSpec.buildUpon(build: TypeSpec.Builder.()->Unit) = toBuilder().also(build).build()