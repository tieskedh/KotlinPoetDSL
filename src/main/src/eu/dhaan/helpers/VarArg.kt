package eu.dhaan.helpers

import kotlin.reflect.KClass

interface ClassWrapper<T:Any> {
    val clazz: KClass<T>
}
data class VarArg<T:Any>(override val clazz: KClass<T>):ClassWrapper<T>
data class Single<T:Any>(override val clazz: KClass<T>):ClassWrapper<T>