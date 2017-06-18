package eu.dhaan.contexts

import com.squareup.kotlinpoet.KotlinFile
import eu.dhaan.constructs.IAccessor
import eu.dhaan.helpers.Single
import eu.dhaan.helpers.VarArg
import kotlin.reflect.KClass

class FileBuilder(pack: String, name: String) {

    infix fun <T : Any> String.of(clazz: KClass<T>) = this to Single(clazz)
    infix fun <T : Any> String.vararg(clazz: KClass<T>) = this to VarArg(clazz)

    private val builder = KotlinFile.builder(pack, name)

    val IAccessor.clazz get() = ClassBuilder(this){builder.addType(it)}
    val IAccessor.func get() = FuncBuilder(this){builder.addFun(it)}
    val clazz get()= ClassBuilder{builder.addType(it)}
    val func get() = FuncBuilder{builder.addFun(it)}

    internal fun build(): KotlinFile {
        return builder.build()
    }
}

fun file(pack: String, name: String, init: FileBuilder.()->Unit): KotlinFile {
    val fileBuilder = FileBuilder(pack, name)
    init(fileBuilder)
    return fileBuilder.build()
}