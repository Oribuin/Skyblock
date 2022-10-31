package xyz.oribuin.skyblock.util

import kotlin.reflect.KClass

// return the type of enum
class EnumIterator(private val enumClass: KClass<out Enum<*>>) : Iterator<Enum<*>> {

    constructor() : this(Enum::class) {
        this.iterator.next()
    }

    private var currentValue = 0
    private var iterator = enumClass.java.enumConstants.iterator()

//    constructor(enumClass: KClass<out Enum<*>>, start: Enum<*>) : this(enumClass) {
//        // if the start is greater than the amount of enums, get first enum
//        if (start.ordinal >= enumClass.java.enumConstants.size) {
//            iterator = enumClass.java.enumConstants.iterator()
//            return
//        }
//
//        for (i in 0 until start.ordinal) {
//            iterator.next()
//        }
//
//    }

    override fun hasNext(): Boolean = iterator.hasNext()

    override fun next(): Enum<*> = iterator.next()

    @Suppress("UNCHECKED_CAST")
    fun <T : Enum<T>> get(): T = enumClass.java.enumConstants[currentValue] as T

    fun skipTo(enum: Enum<*>): Int {
        while (next() != enum) {
            iterator.next()
            currentValue++
        }

        return currentValue
    }

}