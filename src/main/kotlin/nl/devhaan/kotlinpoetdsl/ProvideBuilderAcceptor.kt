package nl.devhaan.kotlinpoetdsl

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import kotlin.reflect.KClass

/**
 * This makes the following code possible
 *
 * clazz() extends String::class{}
 * and func() returns String::Class{}
 *
 * The code uses R, which will be resolved by its surrounding function (extends or returns)
 *
 * This code can unfortunately not be added to the other acceptors, because multiple acceptors make use of it...
 */
interface ProvideBuilderAcceptor{
    operator fun <T : Any, R> KClass<T>.invoke(builder: R.() -> Unit) = this.asTypeName() to builder
    operator fun <R> TypeName.invoke(builder: R.() -> Unit) = this to builder
    operator fun <R> ClassName.invoke(builder: R.() -> Unit) = (this as TypeName) to builder
}