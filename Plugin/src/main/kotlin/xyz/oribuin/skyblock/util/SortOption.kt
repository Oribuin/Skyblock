package xyz.oribuin.skyblock.util

import xyz.oribuin.skyblock.enums.SortType

class SortOption(var sort: SortType, var iterator: Iterator<SortType>) {

    constructor() : this(SortType.NONE, SortType.values().iterator()) {
        this.iterator.next()
    }

    constructor(sort: SortType) : this(sort, sort.javaClass.enumConstants.iterator()) {

        while (iterator.hasNext()) {
            if (iterator.next() == sort) {
                iterator = sort.javaClass.enumConstants.iterator()
                iterator.next()
                break
            }
        }
    }

}