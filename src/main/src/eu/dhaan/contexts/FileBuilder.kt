package eu.dhaan.contexts

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.KotlinFile
import eu.dhaan.constructs.IAccessor
import eu.dhaan.helpers.ClassWrapper
import kotlin.reflect.KClass

class FileBuilder(pack: String, name: String) {

    infix fun <T : Any> String.of(clazz: KClass<T>) = this to ClassWrapper(clazz)
    infix fun <T : Any> String.valOf(clazz: KClass<T>) = this to ClassWrapper(clazz, mutableListOf(KModifier.FINAL))
    infix fun <T : Any> String.vararg(clazz: KClass<T>) = this to ClassWrapper(clazz, mutableListOf(KModifier.VARARG))


    private val builder = KotlinFile.builder(pack, name)

    operator fun  <T: Any> KClass<T>.invoke(builder: CodeBlockBuilder.()->Unit) = this to builder
    infix fun <T : Any> FuncBuilder.returns(pair: Pair<KClass<T>, CodeBlockBuilder.() -> Unit>): FunSpec {
        val (clazz, builder) = pair
        returns(clazz)
        return build(builder)
    }


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