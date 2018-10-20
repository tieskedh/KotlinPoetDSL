package nl.devhaan.kotlinpoetdsl.files

import com.squareup.kotlinpoet.*
import nl.devhaan.kotlinpoetdsl.*
import nl.devhaan.kotlinpoetdsl.`interface`.InterfaceAcceptor
import nl.devhaan.kotlinpoetdsl.classes.ClassAcceptor
import nl.devhaan.kotlinpoetdsl.functions.FunctionAcceptor
import nl.devhaan.kotlinpoetdsl.properties.PropAcceptor

class FileAccessor(
        modifier: MutableSet<KModifier>,
        private val file: FileBuilder
) : Accessor<FileAccessor>(modifier),
        ClassAcceptor by file,
        FunctionAcceptor by file,
        PropAcceptor by file,
        InterfaceAcceptor by file{
    override fun accept(type: TypeSpec) = file.accept(type)
    override fun registerBuilder(builder: IBuilder) = file.registerBuilder(builder)
    override fun unregisterBuilder(builder: IBuilder) = file.unregisterBuilder(builder)
}

class FileBuilder(pack: String, name: String) : ClassAcceptor, FunctionAcceptor, PropAcceptor, ProvideBuilderAcceptor, AccessorContainer<FileAccessor>, IBuilder, InterfaceAcceptor {

    private val builders = mutableSetOf<IBuilder>()
    override fun registerBuilder(builder: IBuilder) {
        builders += builder
    }

    override fun unregisterBuilder(builder: IBuilder) {
        builders -= builder
    }

    override fun finish() {
        builders.forEach(IBuilder::finish)
    }

    private val builder = FileSpec.builder(pack, name)

    override fun accessors(vararg modifier: KModifier) = FileAccessor(modifier.toMutableSet(), this)

    override fun accept(type: TypeSpec) {
        builder.addType(type)
    }

    override fun accept(func: FunSpec) {
        builder.addFunction(func)
    }

    override fun accept(prop: PropertySpec) {
        builder.addProperty(prop)
    }

    internal fun build(): FileSpec {
        builders.forEach { it.finish() }
        return builder.build()
    }
}
/**
* This function is called for generating the file.
* @param pack the package
* @param fileName the name of the file
* @param init the initialization of the file
* @return the file
*/
fun file(pack: String, fileName: String, init: FileBuilder.()->Unit = {}): FileSpec
        = FileBuilder(pack, fileName).also(init).build()