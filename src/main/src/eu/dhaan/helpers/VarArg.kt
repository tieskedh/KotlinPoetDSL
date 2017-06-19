package eu.dhaan.helpers

import com.squareup.kotlinpoet.KModifier
import kotlin.reflect.KClass

class ClassWrapper<T:Any>(
        val clazz: KClass<T>,
        val modifiers: MutableList<KModifier> = mutableListOf<KModifier>()
)