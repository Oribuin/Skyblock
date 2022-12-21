package xyz.oribuin.skyblock.util

import xyz.oribuin.skyblock.enums.FilterType

class FilterOption(var filter: FilterType, var iterator: Iterator<FilterType>) {

    constructor() : this(FilterType.NONE, FilterType.values().iterator()) {
        this.iterator.next()
    }

    constructor(filter: FilterType) : this(filter, filter.javaClass.enumConstants.iterator()) {

        while (iterator.hasNext()) {
            if (iterator.next() == filter) {
                iterator = filter.javaClass.enumConstants.iterator()
                iterator.next()
                break
            }
        }
    }

}