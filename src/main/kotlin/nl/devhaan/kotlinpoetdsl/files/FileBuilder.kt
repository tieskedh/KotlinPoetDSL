package nl.devhaan.kotlinpoetdsl.files

import com.squareup.kotlinpoet.*
import nl.devhaan.kotlinpoetdsl.Accessor
import nl.devhaan.kotlinpoetdsl.AccessorContainer
import nl.devhaan.kotlinpoetdsl.classes.ClassAcceptor
import nl.devhaan.kotlinpoetdsl.functions.FunctionAcceptor

class FileAccessor(
        modifier: MutableSet<KModifier>,
        private val file: FileBuilder
) : Accessor<FileAccessor>(modifier),
        FunctionAcceptor by file,
        ClassAcceptor by file


class FileBuilder(val pack: String, name: String) : FunctionAcceptor, ClassAcceptor, AccessorContainer<FileAccessor> {

    private val builder = FileSpec.builder(pack, name)

    override fun accessors(vararg modifier: KModifier) = FileAccessor(modifier.toMutableSet(), this)

    override fun accept(func: FunSpec) {
        builder.addFunction(func)
    }

    override fun accept(clazz: TypeSpec) {
        builder.addType(clazz)
    }

    internal fun build(): FileSpec = builder.build()
}
/**
* This function is called for generating the file.
* @param pack the package
* @param name the name of the file
* @param init the initialization of the file
* @return the file
*/
fun file(pack: String, name: String, init: FileBuilder.()->Unit = {}): FileSpec
        = FileBuilder(pack, name).also(init).build()