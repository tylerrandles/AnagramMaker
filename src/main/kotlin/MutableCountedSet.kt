import java.util.*

open class MutableCountedSet<K: Comparable<K>>: MutableSet<K>, MutableCollection<K> {

    private val map: MutableMap<K, Int> = mutableMapOf()
    override var size: Int = map.size

    constructor(collection: Collection<K>) {
        addAll(collection)
    }

    constructor(map: Map<K, Int>) {
        this.map.clear()
        this.map.putAll(map)
        this.size = this.map.size

    }

    override fun add(element: K): Boolean {
        map.compute(element , ::inc)
        ++size
        return true
    }

    inline fun apply(block: MutableCountedSet<K>.() -> Unit): MutableCountedSet<K> {
        block(this)
        return this
    }

    private fun inc(key: K, count: Int?): Int = count?.plus(1)?: 1

    fun forEach(action: (key: K) -> Unit) = map.keys.forEach(action)

    fun reduce(action: (acc: K, curr: K) -> K) = map.keys.reduce(action)

    fun filter(action: (matches: K) -> Boolean) = map.keys.filter(action)

    fun joinToString(separator: String): String = StringBuilder().apply {
        toSortedMapByKey().forEach { (key, count) ->
            repeat(count) {
                append(separator + key)
            }
        }
    }.toString().substring(separator.length)

    @Deprecated("after defining the sort the 'toMap' removes the sort by count" +
            "refer to 'keyByMaxCount' for an alternative")
    fun toSortedMapByCount(): SortedMap<K, Int> =
        map.map { it.key to it.value }
            .sortedWith(compareBy( {it.second}, { it.first } ))
            .toMap()
            .toSortedMap()

    private fun toSortedMapByKey() = map.toSortedMap()

    override fun toString() =
        "[${map.toList().joinToString(", ") { "${it.first}:${it.second}" } }]"

    override operator fun contains(element: K): Boolean {
        return map.containsKey(element)
    }

    operator fun contains(set: MutableCountedSet<K>): Boolean {
        return map.all { (key, count) ->
            set[key] <= count
        }
    }

    override fun containsAll(elements: Collection<K>): Boolean {
        return map.keys.containsAll(elements)
    }

    override fun isEmpty(): Boolean {
        return map.isEmpty()
    }

    override fun iterator(): MutableIterator<K> {
        return map.keys.iterator()
    }

    override fun addAll(elements: Collection<K>): Boolean {
        elements.forEach(::add)
        return true
    }

    override fun clear() {
        size = 0
        map.clear()
    }

    override fun remove(element: K): Boolean {
        val count = map.remove(element)
        size -= count?: 0
        return count != null
    }

    override fun removeAll(elements: Collection<K>): Boolean {
        for (element in elements) remove(element)
        return true
    }

    override fun retainAll(elements: Collection<K>): Boolean {
        var kept = false
        val keys = map.keys
        map.clear()
        elements.forEach {
            if (keys.contains(it)) {
                add(it)
                kept = true
            }
        }
        return kept
    }

    // fun maxKeyByCount(): K  = toSortedMapByCount().firstKey()

    fun keyByMaxCount(): K = map.map { it.key to it.value }
        .sortedWith(compareBy( {it.second}, { it.first } )).last().first

    fun maxValue(): Int = map.maxOf(Map.Entry<K, Int>::value)

    fun isSubsetOf(other: MutableCountedSet<K>): Boolean = this.all { this[it] <= other[it] }

    fun isSupersetOf(other: MutableCountedSet<K>): Boolean = this.all { this[it] >= other[it] }

    operator fun get(entry: K): Int = this.map[entry]?: 0

    operator fun set(entry: K , value: Int) {
        map[entry] = value
    }

    operator fun plus(elements: Collection<K>): MutableCountedSet<K> {
        return MutableCountedSet(this).also { addAll(elements) }
    }

    operator fun plusAssign(elements: Collection<K>) {
        this.addAll(MutableCountedSet(elements))
    }

    operator fun minus(that: MutableCountedSet<K>): MutableCountedSet<K> {
        return MutableCountedSet(this).apply {
            for ((k , v) in that.map) {
                val difference = get(k) - v
                this.map[k] = if (difference < 0) 0 else difference
            }
        }
    }

}
