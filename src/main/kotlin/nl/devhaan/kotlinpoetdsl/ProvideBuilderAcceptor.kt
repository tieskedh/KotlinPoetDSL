package nl.devhaan.kotlinpoetdsl

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import kotlin.reflect.KClass

/**
 * This makes the following code possible
 *
 * clazz() extends String::class{}
 * func() returns String::Class{}
 * interf() implements String::class{}
 *
 * The code uses R, which will be resolved by its surrounding function (extends,  returns or implements)
 *
 * This code can unfortunately not be added to the other acceptors, because multiple acceptors make use of it...
 */
interface ProvideBuilderAcceptor{
    class ImplementationData<T>(val typeName: TypeName, val buildScript: T.() -> Unit)
    operator fun <T : Any, R> KClass<T>.invoke(builder: R.() -> Unit) = ImplementationData(asTypeName(), builder)
    operator fun <R> TypeName.invoke(builder: R.() -> Unit) = ImplementationData(this, builder)
    operator fun <R> ClassName.invoke(builder: R.() -> Unit) = ImplementationData(this as TypeName, builder)
}