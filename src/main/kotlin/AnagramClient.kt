import java.nio.file.Files
import java.nio.file.Path

fun getWords() = Files.readAllLines(Path.of("corpus.csv")).map {
    it.split(",")
}.flatten().map {
    MutableCountedSet(it.toList())
}.toSet()

fun getInput() = run {
    val text = readLine()?.lowercase() ?: ""
    val letters = text.split(" ").map {
        it.filter { c -> c in 'a'..'z' }.toList()
    }.flatten()
    MutableCountedSet(letters)
}

fun getSubset(
    given: MutableCountedSet<Char> ,
    dict: Set<MutableCountedSet<Char>>
): Set<MutableCountedSet<Char>> {
    return HashSet<MutableCountedSet<Char>>(64).apply {
        for (word in dict)
            if (word.isSubsetOf(given))
                add(word)
    }.filter { it.getBase().isNotEmpty() }.toSet()
}

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
