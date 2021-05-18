import java.util.*

class MutableCountedSet<K: Comparable<K>>(
    private val given: Collection<K> ,
    private val map: MutableMap<K, Int> = mutableMapOf() ,
    override var size: Int = map.size ,
): MutableSet<K>, MutableCollection<K> {

    init {
        addAll(given)
    }

    /**
     * Add the element to the set and computes its multiplicity
     */
    override fun add(element: K): Boolean {
        map.compute(element , ::inc)
        ++size
        return true
    }

    /**
     * Inline function to initialize a counted set
     *
     * @param block    The function which will define the elements
     * @return         The resulting set
     */
    inline fun apply(block: MutableCountedSet<K>.() -> Unit): MutableCountedSet<K> {
        block(this)
        return this
    }

    /**
     * A function to compute the multiplicity of an element
     * If it does not already exist, the multiplicity will be 1
     * otherwise `count` will be incremented
     */
    private fun inc(key: K, count: Int?): Int {
        return if (count == null) {
            1
        } else {
            count + 1
        }
    }

    /**
     * ForEach
     */
    fun forEach(action: (entry: Map.Entry<K, Int>) -> Unit) = map.forEach(action)

    /**
     * Reduce
     */
    fun reduce(action: (acc: Map.Entry<K, Int>, curr: Map.Entry<K, Int>) -> Map.Entry<K, Int>): Map.Entry<K, Int> {
        var curr = this.map.toMap().entries.first()
        for (entry in this.map) {
            curr = action(curr, entry)
        }
        return curr
    }

    /**
     * Filter
     */
    fun filter(action: (matches: Map.Entry<K, Int>,) -> Boolean) = map.filter(action)

    /**
     * Map
     */
    fun <R> map(action: (Map.Entry<K, Int>,) -> R) = map.map(action)

    /**
     * Custom join to string function
     */
    fun joinToString(separator: String): String = StringBuilder().apply {
        toSortedMapByKey().forEach { (key, count) ->
            repeat(count) {
                append(separator + key)
            }
        }
    }.toString().substring(separator.length)

    /**
     * Cannot return a sorted map by values, refer to `keyByMaxCount`
     */
    @Deprecated(message =
    "after defining the sort the 'toMap' removes the sort by count refer to 'keyByMaxCount' for an alternative"
    )
    fun toSortedMapByCount(): SortedMap<K, Int> =
        map.map { it.key to it.value }
            .sortedWith(compareBy( {it.second}, { it.first } ))
            .toMap()
            .toSortedMap()

    /**
     *
     */
    private fun toSortedMapByKey() = map.toSortedMap()

    /**
     * Returns true if the map contains the element, otherwise false
     */
    override operator fun contains(element: K): Boolean {
        return map.containsKey(element)
    }

    /**
     * Returns true if the
     */
    operator fun contains(other: MutableCountedSet<K>): Boolean {
        for (otherKey in other.map.keys) {
            if (!this.map.containsKey(otherKey) || this[otherKey] < other[otherKey]) {
                return false
            }
        }
        return true
    }

    /**
     * Returns true if all of the given elements exist within the map
     */
    override fun containsAll(elements: Collection<K>): Boolean {
        return map.keys.containsAll(elements)
    }

    /**
     * Returns true if the set is empty (has no elements), otherwise false
     */
    override fun isEmpty(): Boolean {
        return map.isEmpty()
    }

    /**
     * Returns an iterator over the keys
     */
    override fun iterator(): MutableIterator<K> {
        return map.keys.iterator()
    }

    /**
     * Adds all of the given elements to the set
     */
    override fun addAll(elements: Collection<K>): Boolean {
        elements.forEach(::add)
        return true
    }

    /**
     * Removes all elements, sets size to 0
     */
    override fun clear() {
        size = 0
        map.clear()
    }

    /**
     * Removes a single instance of the element from the set
     * If the resulting number is `0` the element will be removed
     * from the set entirely
     */
    override fun remove(element: K): Boolean {
        var count = map.remove(element)
        if (count != null) {
            --size
            --count
            if (count > 0) {
                map[element] = count
            }
        }
        return true
    }

    /**
     * Removes all of the given elements
     */
    override fun removeAll(elements: Collection<K>): Boolean {
        for (key in elements) {
            remove(key)
        }
        return true
    }

    /**
     * Retains only the elements in this collection that are contained in the specified collection.
     * Returns:
     * true if any element was removed from the collection, false if the collection was not modified.
     */
    override fun retainAll(elements: Collection<K>): Boolean {
        var removed = true
        val keys = map.keys
        this.clear()
        elements.forEach {
            if (keys.contains(it)) {
                add(it)
                removed = false
            }
        }
        return removed
    }

    /**
     * Returns the key with the highest multiplicity
     */
    fun keyByMaxCount(): K = map.map {
        it.key to it.value
    }.sortedWith(
        compareBy( { it.second }, { it.first } )
    ).last().first

    /**
     * Returns the highest number of occurrences
     */
    fun maxValue(): Int = map.maxOf(Map.Entry<K, Int>::value)

    /**
     * Determines if all values are less than or equal to other values for all keys
     */
    fun isSubsetOf(other: MutableCountedSet<K>): Boolean = this.all { this[it] <= other[it] }

    /**
     * Determines if all values are greater than or equal to other values for all keys
     */
    fun isSupersetOf(other: MutableCountedSet<K>): Boolean = this.all { this[it] >= other[it] }

    /**
     * Operator function to get entries
     */
    operator fun get(key: K): Int = this.map[key]?: 0

    /**
     * Operator function to define entries
     */
    operator fun set(key: K , value: Int) {
        repeat(value) {
            this.add(key)
        }
    }

    /**
     * Returns a new counted set combining the current elements with the given elements
     */
    operator fun plus(keys: Collection<K>): MutableCountedSet<K> {
        return MutableCountedSet(this).also { addAll(keys) }
    }

    /**
     * Adds all of the elements to the current set
     */
    operator fun plusAssign(keys: Collection<K>) {
        this.addAll(MutableCountedSet(keys))
    }

    /**
     * Removes all of the keys from the current set
     */
    operator fun minus(that: MutableCountedSet<K>): MutableCountedSet<K> {
        for ((key, count) in that.map) {
            repeat(count) {
                this - key
            }
        }
        return this
    }

    /**
     * Operator function for removing an element from the set
     *
     * @param key    The given k
     * @return       True unless exception thrown
     */
    operator fun minus(key: K): Boolean {
        return remove(key)
    }

    /**
     * Given parameter as a string
     */
    fun getBase(): String = given.joinToString("")

    /**
     * Keys for deconstruction
     */
    operator fun component1(): MutableSet<K> = map.keys

    /**
     * Corresponding values to keys for deconstruction
     */
    operator fun component2(): MutableCollection<Int> = map.values

    /**
     * Description of the object
     */
    override fun toString() = "[${map.toList().joinToString(", ") { "${ it.first }:${ it.second }" } }]"

}
