package nl.devhaan.kotlinpoetdsl.inheritance

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import nl.devhaan.kotlinpoetdsl.EMPTY_CODEBLOCK
import nl.devhaan.kotlinpoetdsl.classes.ClassBuilder
import kotlin.reflect.KClass

class ImplementationDSL(val classBuilder: ClassBuilder) {
    class DelegationProperty(val name: String)

    operator fun KClass<*>.invoke(format: String? = null, vararg parts: Any?) {
        val codeBlock = format?.let { CodeBlock.of(it, *parts) } ?: EMPTY_CODEBLOCK
        classBuilder.addImplement(this.asTypeName(), codeBlock)
    }

    operator fun TypeName.invoke(format: String? = null, vararg parts: Any?){
        val codeBlock = format?.let { CodeBlock.of(it, *parts) } ?: EMPTY_CODEBLOCK
        classBuilder.addImplement(this, codeBlock)
    }
    operator fun KClass<*>.invoke(delVar: DelegationProperty) {
        classBuilder.addImplement(this.asTypeName(), delVar.name)
    }

    fun delVar(name: String) = DelegationProperty(name)
}