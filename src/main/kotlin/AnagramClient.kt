import java.nio.file.Files
import java.nio.file.Path
import java.util.*

/*
TODO
 clean up
 1) add comments
 2) remove unused
 10MAY21
 work on set union, difference, intersection

 */

/*
 3.1)
 */
fun getWords() = Files.readAllLines(Path.of("corpus.csv")).map {
    it.split(",")
}.flatten().map {
    MutableCountedSet(it.toList())
}.toSet()

/*
1), 2)
*/
fun getInput() = run {
    val text = readLine()?.lowercase() ?: ""
    val letters = text.split(" ").map {
        it.filter { c -> c in 'a'..'z' }.toList()
    }.flatten()
    MutableCountedSet(letters)
}

/*
3.2)
*/
fun getSubset(
    given: MutableCountedSet<Char> ,
    dict: Set<MutableCountedSet<Char>>
): Set<MutableCountedSet<Char>> {
    return mutableSetOf<MutableCountedSet<Char>>().apply {
        for (word in dict)
            if (word.isSubsetOf(given))
                add(word)
    }.filter { it.getBase().isNotEmpty() }.toSet()
}

/*
4)
*/
fun reduce(remaining: MutableCountedSet<Char>) {
    var words = getSubset(remaining , getWords())
    while (remaining.isNotEmpty() && words.isNotEmpty() && words.isNotEmpty()) {
        val selection = words.random()
        print(selection.getBase())
        remaining.minus(selection)
        words = getSubset(remaining , words)
        print(' ')
    }
    println()
}

fun main() {
    val lines = readLine()?.toInt() ?: 1
    val set = getInput()
    for (i in 1 until lines) {
        set += getInput()
    }
    println(set)
    reduce(set)
}

