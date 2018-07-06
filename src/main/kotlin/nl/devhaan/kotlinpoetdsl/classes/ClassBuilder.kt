package nl.devhaan.kotlinpoetdsl.classes


import com.squareup.kotlinpoet.*
import nl.devhaan.kotlinpoetdsl.*
import nl.devhaan.kotlinpoetdsl.functions.FunctionAcceptor
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

    override fun accept(prop: PropertySpec) {
        builder.addProperty(prop)
    }

    override fun accessors(vararg modifier: KModifier) = ClassAccessor(modifier.toMutableSet(), this)

    private lateinit var builder: TypeSpec.Builder

    operator fun invoke(name: String, vararg variables: Variable, init: ClassBuilder.() -> Unit = {}) = build(name){
        if (variables.isNotEmpty()) primaryConstructor(variables)
        init(this)
    }

    fun primaryConstructor(variables: Array<out Variable>) {
        val primBuilder= FunSpec.constructorBuilder()
        variables.forEach { variable ->
            variable.mutable?.also { prop(variable.copy(initializer = CodeBlock.of(variable.name))) }
            primBuilder.addParameter(variable.toParamSpec())
        }
        builder.primaryConstructor(primBuilder.build())
    }


    private inline fun build(name: String, buildScript: ClassBuilder.() -> Unit = {}): TypeSpec {
        builder = TypeSpec.classBuilder(name).also { it.addModifiers(*accessor.modifiers) }
        buildScript(this)
        return builder.build().also(adding)
    }
}

fun TypeSpec.buildUpon(build: TypeSpec.Builder.()->Unit) = toBuilder().also(build).build()