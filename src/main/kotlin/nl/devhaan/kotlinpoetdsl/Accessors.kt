package nl.devhaan.kotlinpoetdsl

import com.squareup.kotlinpoet.KModifier

@Level
interface AccessorContainer<out T : IAccessor<T>>{
    fun accessors(vararg modifier: KModifier) : T
}

fun IAccessor<*>?.orEmpty() = this ?: PlainAccessor()
interface IAccessor<out T : IAccessor<T>>{
    val public      : T
    val private     : T
    val protected   : T
    val internal    : T
    val operator    : T
    val open        : T
    val abstract    : T
    val inline      : T
    val const       : T
    val inner       : T
    val annotation  : T
    val data        : T
    val enum        : T
    val external    : T
    val final       : T
    val infix       : T
    val lateInit    : T
    val override    : T
    val sealed      : T
    val suspend     : T
    val tailRec     : T
    val modifiers        : Array<KModifier>
}

inline val <R, T : AccessorContainer<R>> T.abstract   get() = accessors(KModifier.ABSTRACT)
inline val <R, T : AccessorContainer<R>> T.const      get() = accessors(KModifier.CONST)
inline val <R, T : AccessorContainer<R>> T.annotation get() = accessors(KModifier.ANNOTATION)
inline val <R, T : AccessorContainer<R>> T.data       get() = accessors(KModifier.DATA)
inline val <R, T : AccessorContainer<R>> T.enum       get() = accessors(KModifier.ENUM)
inline val <R, T : AccessorContainer<R>> T.external   get() = accessors(KModifier.EXTERNAL)
inline val <R, T : AccessorContainer<R>> T.final      get() = accessors(KModifier.FINAL)
inline val <R, T : AccessorContainer<R>> T.infix      get() = accessors(KModifier.INFIX)
inline val <R, T : AccessorContainer<R>> T.lateInit   get() = accessors(KModifier.LATEINIT)
inline val <R, T : AccessorContainer<R>> T.override   get() = accessors(KModifier.OVERRIDE)
inline val <R, T : AccessorContainer<R>> T.sealed     get() = accessors(KModifier.SEALED)
inline val <R, T : AccessorContainer<R>> T.suspend    get() = accessors(KModifier.SUSPEND)
inline val <R, T : AccessorContainer<R>> T.tailRec    get() = accessors(KModifier.TAILREC)
inline val <R, T : AccessorContainer<R>> T.open       get() = accessors(KModifier.OPEN)
inline val <R, T : AccessorContainer<R>> T.inner      get() = accessors(KModifier.INNER)
inline val <R, T : AccessorContainer<R>> T.public     get() = accessors(KModifier.PUBLIC)
inline val <R, T : AccessorContainer<R>> T.private    get() = accessors(KModifier.PRIVATE)
inline val <R, T : AccessorContainer<R>> T.protected  get() = accessors(KModifier.PROTECTED)
inline val <R, T : AccessorContainer<R>> T.internal   get() = accessors(KModifier.INTERNAL)
inline val <R, T : AccessorContainer<R>> T.inline     get() = accessors(KModifier.INLINE)
inline val <R, T : AccessorContainer<R>> T.operator   get() = accessors(KModifier.OPERATOR)


fun Set<KModifier>.toAccessor()= PlainAccessor(toMutableSet())
@JvmName("mutableToAccessor")
fun MutableSet<KModifier>.toAccessor()= PlainAccessor(this)
fun Array<KModifier>.toAccessor() = toMutableSet().toAccessor()

class PlainAccessor(accessors: MutableSet<KModifier> = mutableSetOf()) : Accessor<PlainAccessor>(accessors)
abstract class Accessor<out T : Accessor<T>> internal constructor(private val accessors: MutableSet<KModifier> = mutableSetOf()): IAccessor<T> {
    override val abstract   get() = getValue(KModifier.ABSTRACT)
    override val const      get() = getValue(KModifier.CONST)
    override val annotation get() = getValue(KModifier.ANNOTATION)
    override val data       get() = getValue(KModifier.DATA)
    override val enum       get() = getValue(KModifier.ENUM)
    override val external   get() = getValue(KModifier.EXTERNAL)
    override val final      get() = getValue(KModifier.FINAL)
    override val infix      get() = getValue(KModifier.INFIX)
    override val lateInit   get() = getValue(KModifier.LATEINIT)
    override val override   get() = getValue(KModifier.OVERRIDE)
    override val sealed     get() = getValue(KModifier.SEALED)
    override val suspend    get() = getValue(KModifier.SUSPEND)
    override val tailRec    get() = getValue(KModifier.TAILREC)
    override val open       get() = getValue(KModifier.OPEN)
    override val inner      get() = getValue(KModifier.INNER)
    override val public     get() = getValue(KModifier.PUBLIC)
    override val private    get() = getValue(KModifier.PRIVATE)
    override val protected  get() = getValue(KModifier.PROTECTED)
    override val internal   get() = getValue(KModifier.INTERNAL)
    override val inline     get() = getValue(KModifier.INLINE)
    override val operator   get() = getValue(KModifier.OPERATOR)

    fun getValue(modifier: KModifier) : T {
        accessors += modifier
        @Suppress("UNCHECKED_CAST")
        return this as T
    }

    override val modifiers get() = accessors.toTypedArray()
}


class ModifierEditorDSL(var existing: Set<KModifier>){
    val empty = setOf<KModifier>()

    operator fun IAccessor<*>.unaryPlus() = existing + this

    operator fun IAccessor<*>.invoke() = modifiers.toSet()

    operator fun Set<KModifier>.minus(accessor: IAccessor<*>) =
            this - accessor.modifiers

    operator fun Set<KModifier>.plus(accessor: IAccessor<*>) =
            this + accessor.modifiers
}
